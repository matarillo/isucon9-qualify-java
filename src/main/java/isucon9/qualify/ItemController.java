package isucon9.qualify;

import static isucon9.qualify.Const.ItemsPerPage;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import isucon9.qualify.data.DataService;
import isucon9.qualify.dto.Category;
import isucon9.qualify.dto.ErrorResponse;
import isucon9.qualify.dto.Item;
import isucon9.qualify.dto.ItemDetail;
import isucon9.qualify.dto.ItemSimple;
import isucon9.qualify.dto.NewItemsResponse;
import isucon9.qualify.dto.Shipping;
import isucon9.qualify.dto.User;
import isucon9.qualify.dto.UserSimple;
import isucon9.qualify.web.SessionService;

@RestController
public class ItemController {

    private final SessionService sessionService;
    private final DataService dataService;

    public ItemController(SessionService sessionService, DataService dataService) {
        this.sessionService = sessionService;
        this.dataService = dataService;
    }

    @GetMapping("/new_items.json")
    public NewItemsResponse index(
            @RequestParam(name = "item_id", defaultValue = "0") long itemId,
            @RequestParam(name = "created_at", defaultValue = "0") long createdAtTimestamp) {
        List<Item> items;
        if (itemId > 0 && createdAtTimestamp > 0) {
            LocalDateTime createdAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(createdAtTimestamp),
                    ZoneOffset.UTC);
            items = dataService.getItems(createdAt, itemId);
        } else {
            items = dataService.getItems();
        }
        List<ItemSimple> itemSimples = new ArrayList<ItemSimple>();
        for (Item item : items) {
            UserSimple seller = dataService.getUserSimpleById(item.getSellerId())
                    .orElseThrow(notFound("seller not found"));
            Category category = dataService.GetCategoryById(item.getCategoryId())
                    .orElseThrow(notFound("category not found"));
            ItemSimple itemSimple = new ItemSimple();
            itemSimple.setId(item.getId());
            itemSimple.setSellerId(item.getSellerId());
            itemSimple.setSeller(seller);
            itemSimple.setStatus(item.getStatus());
            itemSimple.setName(item.getName());
            itemSimple.setPrice(item.getPrice());
            itemSimple.setImageUrl(getImageUrl(item.getImageName()));
            itemSimple.setCategoryId(item.getCategoryId());
            itemSimple.setCategory(category);
            itemSimple.setCreatedAt(item.getCreatedAt().toInstant(ZoneOffset.UTC).getEpochSecond());
            itemSimples.add(itemSimple);
        }
        boolean hasNext = false;
        if (itemSimples.size() > ItemsPerPage) {
            hasNext = true;
            itemSimples = itemSimples.subList(0, ItemsPerPage);
        }

        NewItemsResponse newItems = new NewItemsResponse();
        newItems.setItems(itemSimples);
        newItems.setHasNext(hasNext);
        return newItems;
    }

    private String getImageUrl(String imageName) {
        return "/upload/" + imageName;
    }

    @GetMapping("/items/{item_id}.json")
    public ItemDetail getItem(@PathVariable("item_id") long itemId) {
        User user = getUser().orElseThrow(notFound("user not found"));
        Item item = dataService.getItemById(itemId).orElseThrow(notFound("item not found"));
        Category category = dataService.GetCategoryById(item.getCategoryId())
                .orElseThrow(notFound("category not found"));
        UserSimple seller = dataService.getUserSimpleById(item.getSellerId()).orElseThrow(notFound("seller not found"));

        ItemDetail itemDetail = new ItemDetail();
        itemDetail.setId(item.getId());
        itemDetail.setSellerId(item.getSellerId());
        itemDetail.setSeller(seller);
        itemDetail.setStatus(item.getStatus());
        itemDetail.setName(item.getName());
        itemDetail.setPrice(item.getPrice());
        itemDetail.setDescription(item.getDescription());
        itemDetail.setImageUrl(getImageUrl(item.getImageName()));
        itemDetail.setCategoryId(item.getCategoryId());
        itemDetail.setCategory(category);
        itemDetail.setCreatedAt(item.getCreatedAt().toInstant(ZoneOffset.UTC).getEpochSecond());

        if ((user.getId() == item.getSellerId() || user.getId() == item.getBuyerId()) && item.getBuyerId() > 0) {
            UserSimple buyer = dataService.getUserSimpleById(item.getBuyerId())
                    .orElseThrow(notFound("buyer not found"));
            itemDetail.setBuyerId(buyer.getId());
            itemDetail.setBuyer(buyer);

            dataService.getTransactionEvidenceByItemId(item.getId()).ifPresent(evidence -> {
                Shipping shipping = dataService.getShippingById(evidence.getId())
                        .orElseThrow(notFound("shipping not found"));
                itemDetail.setTransactionEvidenceId(evidence.getId());
                itemDetail.setTransactionEvidenceStatus(evidence.getStatus());
                itemDetail.setShippingStatus(shipping.getStatus());
            });
        }

        return itemDetail;
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
        body.setError(e.getName() + " param error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
