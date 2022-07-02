package isucon9.qualify;

import static isucon9.qualify.Const.ItemsPerPage;
import static isucon9.qualify.Const.TransactionsPerPage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import isucon9.qualify.api.ApiService;
import isucon9.qualify.data.DataService;
import isucon9.qualify.dto.ApiShipmentStatusRequest;
import isucon9.qualify.dto.ApiShipmentStatusResponse;
import isucon9.qualify.dto.Category;
import isucon9.qualify.dto.ErrorResponse;
import isucon9.qualify.dto.Item;
import isucon9.qualify.dto.ItemDetail;
import isucon9.qualify.dto.ItemSimple;
import isucon9.qualify.dto.Shipping;
import isucon9.qualify.dto.TransactionEvidence;
import isucon9.qualify.dto.TransactionsResponse;
import isucon9.qualify.dto.User;
import isucon9.qualify.dto.UserItemsResponse;
import isucon9.qualify.dto.UserSimple;
import isucon9.qualify.web.SessionService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final SessionService sessionService;
    private final DataService dataService;
    private final TransactionTemplate tx;
    private final ApiService apiService;

    public UserController(SessionService sessionService, DataService dataService, TransactionTemplate tx,
            ApiService apiService) {
        this.sessionService = sessionService;
        this.dataService = dataService;
        this.tx = tx;
        this.apiService = apiService;
    }

    @GetMapping("/{user_id}.json")
    public UserItemsResponse getUserItems(@PathVariable("user_id") long userId,
            @RequestParam(name = "item_id") Optional<Long> itemId,
            @RequestParam(name = "created_at") Optional<Long> createdAtTimestamp) {
        throwIfNotPositiveValue(Optional.of(userId), "incorrect user id");
        throwIfNotPositiveValue(itemId, "item_id param error");
        throwIfNotPositiveValue(createdAtTimestamp, "created_at param error");

        UserSimple userSimple = dataService.getUserSimpleById(userId).orElseThrow(notFound("user not found"));

        List<Item> items;
        if (itemId.isPresent() && createdAtTimestamp.isPresent()) {
            LocalDateTime createdAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(createdAtTimestamp.get()),
                    ZoneOffset.UTC);
            items = dataService.getItemsForSale(userSimple.getId(), createdAt, itemId.get());
        } else {
            items = dataService.getItemsForSale(userSimple.getId());
        }

        List<ItemSimple> itemSimples = new ArrayList<ItemSimple>();
        for (Item item : items) {
            Category category = dataService.GetCategoryById(item.getCategoryId())
                    .orElseThrow(notFound("category not found"));
            ItemSimple itemSimple = new ItemSimple();
            itemSimple.setId(item.getId());
            itemSimple.setSellerId(item.getSellerId());
            itemSimple.setSeller(userSimple);
            itemSimple.setStatus(item.getStatus());
            itemSimple.setName(item.getName());
            itemSimple.setPrice(item.getPrice());
            itemSimple.setImageUrl(getImageUrl(item.getImageName()));
            itemSimple.setCategoryId(item.getCategoryId());
            itemSimple.setCategory(category);
            itemSimple.setCreatedAt(item.getCreatedAt().toEpochSecond(ZoneOffset.UTC));
            itemSimples.add(itemSimple);
        }

        boolean hasNext = false;
        if (itemSimples.size() > ItemsPerPage) {
            hasNext = true;
            itemSimples = itemSimples.subList(0, ItemsPerPage);
        }

        UserItemsResponse userItems = new UserItemsResponse();
        userItems.setUser(userSimple);
        userItems.setItems(itemSimples);
        userItems.setHasNext(hasNext);
        return userItems;
    }

    private void throwIfNotPositiveValue(Optional<Long> id, String message) {
        if (id.isPresent() && id.get() <= 0L) {
            throw new ApiException(message, HttpStatus.BAD_REQUEST);
        }
    }

    private String getImageUrl(String imageName) {
        return "/upload/" + imageName;
    }

    @GetMapping("/transactions.json")
    public TransactionsResponse getTransactions(
            @RequestParam(name = "item_id") Optional<Long> itemId,
            @RequestParam(name = "created_at") Optional<Long> createdAtTimestamp) {
        User user = getUser().orElseThrow(notFound("user not found"));
        throwIfNotPositiveValue(itemId, "item_id param error");
        throwIfNotPositiveValue(createdAtTimestamp, "created_at param error");

        List<ItemDetail> itemDetails = tx.execute(status -> {
            List<Item> items;
            if (itemId.isPresent() && createdAtTimestamp.isPresent()) {
                LocalDateTime createdAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(createdAtTimestamp.get()),
                        ZoneOffset.UTC);
                items = dataService.getTransactionItems(user.getId(), createdAt, itemId.get());
            } else {
                items = dataService.getTransactionItems(user.getId());
            }
            List<ItemDetail> details = new ArrayList<ItemDetail>();
            for (Item item : items) {
                UserSimple seller = dataService.getUserSimpleById(item.getSellerId())
                        .orElseThrow(notFound("seller not found"));
                Category category = dataService.GetCategoryById(item.getCategoryId())
                        .orElseThrow(notFound("category not found"));
                ItemDetail itemDetail = new ItemDetail();
                itemDetail.setId(item.getId());
                itemDetail.setSellerId(item.getSellerId());
                itemDetail.setSeller(seller);
                itemDetail.setStatus(item.getStatus());
                itemDetail.setName(item.getName());
                itemDetail.setDescription(item.getDescription());
                itemDetail.setImageUrl(getImageUrl(item.getImageName()));
                itemDetail.setCategoryId(item.getCategoryId());
                itemDetail.setCategory(category);
                itemDetail.setCreatedAt(item.getCreatedAt().toEpochSecond(ZoneOffset.UTC));
                if (item.getBuyerId() > 0) {
                    UserSimple buyer = dataService.getUserSimpleById(item.getBuyerId())
                            .orElseThrow(notFound("buyer not found"));
                    itemDetail.setBuyerId(item.getBuyerId());
                    itemDetail.setBuyer(buyer);
                }

                Optional<TransactionEvidence> transactionEvidence = dataService
                        .getTransactionEvidenceByItemId(item.getId());
                transactionEvidence.ifPresent(evidence -> {
                    Shipping shipping = dataService.getShippingById(evidence.getId())
                            .orElseThrow(notFound("shipping not found"));
                    ApiShipmentStatusRequest req = new ApiShipmentStatusRequest();
                    req.setReserveId(shipping.getReserveId());
                    ApiShipmentStatusResponse res = apiService.getShipmentStatus(dataService.getShipmentServiceURL(),
                            req);
                    itemDetail.setTransactionEvidenceId(evidence.getId());
                    itemDetail.setTransactionEvidenceStatus(evidence.getStatus());
                    itemDetail.setShippingStatus(res.getStatus());
                });

                details.add(itemDetail);
            }
            return details;
        });

        boolean hasNext = false;
        if (itemDetails.size() > TransactionsPerPage) {
            hasNext = true;
            itemDetails = itemDetails.subList(0, TransactionsPerPage);
        }

        TransactionsResponse transactions = new TransactionsResponse();
        transactions.setItems(itemDetails);
        transactions.setHasNext(hasNext);
        return transactions;
    }

    private Optional<User> getUser() {
        return dataService.getUserById(sessionService.getUserId());
    }

    private Supplier<ApiException> notFound(String message) {
        return () -> new ApiException(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException e) {
        ErrorResponse body = new ErrorResponse();
        body.setError(e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        ErrorResponse body = new ErrorResponse();
        String name = e.getName();
        String message = (name.equals("user_id") ? "incorrect user id" : name + " param errpr");
        body.setError(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
