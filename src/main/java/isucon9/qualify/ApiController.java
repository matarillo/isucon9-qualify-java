package isucon9.qualify;

import static isucon9.qualify.Const.BumpChargeSeconds;
import static isucon9.qualify.Const.ItemMaxPrice;
import static isucon9.qualify.Const.ItemMinPrice;
import static isucon9.qualify.Const.ItemPriceErrMsg;
import static isucon9.qualify.Const.ItemsPerPage;
import static isucon9.qualify.Const.TransactionsPerPage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import isucon9.qualify.api.ApiService;
import isucon9.qualify.data.DataService;
import isucon9.qualify.dto.ApiShipmentStatusRequest;
import isucon9.qualify.dto.ApiShipmentStatusResponse;
import isucon9.qualify.dto.Category;
import isucon9.qualify.dto.ErrorResponse;
import isucon9.qualify.dto.Item;
import isucon9.qualify.dto.ItemDetail;
import isucon9.qualify.dto.ItemSimple;
import isucon9.qualify.dto.LoginRequest;
import isucon9.qualify.dto.NewItemsResponse;
import isucon9.qualify.dto.SettingResponse;
import isucon9.qualify.dto.Shipping;
import isucon9.qualify.dto.TransactionEvidence;
import isucon9.qualify.dto.TransactionsResponse;
import isucon9.qualify.dto.User;
import isucon9.qualify.dto.UserItemsResponse;
import isucon9.qualify.dto.UserSimple;
import isucon9.qualify.web.SessionService;

@RestController
public class ApiController {

    private final Logger logger = LoggerFactory.getLogger(ApiController.class);
    private final SessionService sessionService;
    private final DataService dataService;
    private final TransactionTemplate tx;
    private final ApiService apiService;

    public ApiController(SessionService sessionService, DataService dataService, TransactionTemplate tx,
            ApiService apiService) {
        this.sessionService = sessionService;
        this.dataService = dataService;
        this.tx = tx;
        this.apiService = apiService;
    }

    // mux.HandleFunc(pat.Post("/initialize"), postInitialize)

    @GetMapping("/new_items.json")
    public NewItemsResponse index(
            @RequestParam(name = "item_id", defaultValue = "0") long itemId,
            @RequestParam(name = "created_at", defaultValue = "0") long createdAtTimestamp) {
        throwIfNotPositiveValue(itemId, "item_id param error");
        throwIfNotPositiveValue(createdAtTimestamp, "created_at param error");

        List<Item> items;
        if (itemId > 0L && createdAtTimestamp > 0L) {
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

    @GetMapping("/new_items/{root_category_id}.json")
    public NewItemsResponse getNewCategoryItems(@PathVariable("root_category_id") int rootCategoryId,
            @RequestParam(name = "item_id", defaultValue = "0") long itemId,
            @RequestParam(name = "created_at", defaultValue = "0") long createdAtTimestamp) {
        throwIfNotPositiveValue(itemId, "item_id param error");
        throwIfNotPositiveValue(createdAtTimestamp, "created_at param error");

        Category rootCategory = dataService.GetCategoryById(rootCategoryId).orElseThrow(notFound("category not found"));
        if (rootCategory.getParentId() != 0) {
            throw new ApiException("category not found", HttpStatus.NOT_FOUND);
        }
        List<Integer> categoryIds = dataService.getCategoryIdsByRootCategoryId(rootCategory.getId());
        List<Item> items;
        if (itemId > 0L && createdAtTimestamp > 0L) {
            LocalDateTime createdAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(createdAtTimestamp),
                    ZoneOffset.UTC);
            items = dataService.getItemsByCategoryIds(categoryIds, createdAt, itemId);
        } else {
            items = dataService.getItemsByCategoryIds(categoryIds);
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
        newItems.setRootCategoryId(rootCategory.getId());
        newItems.setRootCategoryName(rootCategory.getCategoryName());
        newItems.setItems(itemSimples);
        newItems.setHasNext(hasNext);
        return newItems;
    }

    @GetMapping("/users/transactions.json")
    public TransactionsResponse getTransactions(
            @RequestParam(name = "item_id", defaultValue = "0") long itemId,
            @RequestParam(name = "created_at", defaultValue = "0") long createdAtTimestamp) {
        throwIfNotPositiveValue(itemId, "item_id param error");
        throwIfNotPositiveValue(createdAtTimestamp, "created_at param error");

        User user = getUser().orElseThrow(notFound("user not found"));
        List<ItemDetail> itemDetails = tx.execute(status -> {
            List<Item> items;
            if (itemId > 0L && createdAtTimestamp > 0L) {
                LocalDateTime createdAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(createdAtTimestamp),
                        ZoneOffset.UTC);
                items = dataService.getTransactionItems(user.getId(), createdAt, itemId);
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

    @GetMapping("/users/{user_id}.json")
    public UserItemsResponse getUserItems(@PathVariable("user_id") long userId,
            @RequestParam(name = "item_id", defaultValue = "0") long itemId,
            @RequestParam(name = "created_at", defaultValue = "0") long createdAtTimestamp) {
        throwIfNotPositiveValue(itemId, "item_id param error");
        throwIfNotPositiveValue(createdAtTimestamp, "created_at param error");

        throwIfNotPositiveValue(userId, "incorrect user id");
        UserSimple userSimple = dataService.getUserSimpleById(userId).orElseThrow(notFound("user not found"));

        List<Item> items;
        if (itemId > 0L && createdAtTimestamp > 0L) {
            LocalDateTime createdAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(createdAtTimestamp),
                    ZoneOffset.UTC);
            items = dataService.getItemsForSale(userSimple.getId(), createdAt, itemId);
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

    @GetMapping("/items/{item_id}.json")
    public ItemDetail getItem(@PathVariable("item_id") long itemId) {
        throwIfNotPositiveValue(itemId, "incorrect item id");

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

    private void throwIfNotPositiveValue(long id, String message) {
        if (id < 0L) {
            throw new ApiException(message, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/items/edit")
    public Object postItemEdit() {
        return null;
    }

    // mux.HandleFunc(pat.Post("/buy"), postBuy)
    // mux.HandleFunc(pat.Post("/sell"), postSell)

    @PostMapping(value = "/sell", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String postSell(@RequestParam("csrf_token") String csrfToken, @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") int price,
            @RequestParam("category_id") int categoryId,
            @RequestParam("image") MultipartFile image) {
        if (categoryId < 0) {
            throw new ApiException("category id error", HttpStatus.BAD_REQUEST);
        }
        if (name.isEmpty() || description.isEmpty() || price == 0 || categoryId == 0) {
            throw new ApiException("all parameters are required", HttpStatus.BAD_REQUEST);
        }
        if (price < ItemMinPrice || price > ItemMaxPrice) {
            throw new ApiException(ItemPriceErrMsg, HttpStatus.BAD_REQUEST);
        }

        Category category = dataService.GetCategoryById(categoryId).orElseThrow(() -> new ApiException("Incorrect category ID", HttpStatus.BAD_REQUEST));
        User user = getUser().orElseThrow(notFound("user not found"));

        String contentType = image.getContentType();
        String ext;
        switch (contentType) {
            case "image/jpeg":
                ext = ".jpg";
                break;
            case "image/png":
                ext = ".png";
                break;
            case "image/gif":
                ext = ".gif";
                break;
            default:
                throw new ApiException("unsupported image format error", HttpStatus.BAD_REQUEST);
        }
        String imgName = secureRandomStr(16) + ext;
        try {
            ClassPathResource uploadDirResource = new ClassPathResource("static/upload");
            Path uploadDirPath = uploadDirResource.getFile().toPath();
            Path target = uploadDirPath.resolve(imgName);
            Path source = image.getResource().getFile().toPath();
            Files.move(source, target);
        } catch (IOException e) {
            throw new ApiException("Saving image failed", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }

        Object o = tx.execute(status -> {
            User seller = dataService.getUserByIdForUpdate(user.getId()).orElseThrow(notFound("user not found"));
            LocalDateTime now = LocalDateTime.now();
            // last_bump + 3s > now
            LocalDateTime waitExpirationTime = seller.getLastBump().plus(BumpChargeSeconds);
            if (waitExpirationTime.isAfter(now)) {
                throw new ApiException("Bump not allowed", HttpStatus.FORBIDDEN);
            }

            return null;
        });

        return imgName;
    }

    // mux.HandleFunc(pat.Post("/ship"), postShip)
    // mux.HandleFunc(pat.Post("/ship_done"), postShipDone)
    // mux.HandleFunc(pat.Post("/complete"), postComplete)
    // mux.HandleFunc(pat.Get("/transactions/:transaction_evidence_id.png"),
    // getQRCode)
    // mux.HandleFunc(pat.Post("/bump"), postBump)

    @GetMapping("/settings")
    public SettingResponse getSettings() {
        SettingResponse s = new SettingResponse();
        s.setCsrfToken(sessionService.getCsrfToken());
        getUser().ifPresent(user -> s.setUser(user));
        s.setPaymentServiceUrl(dataService.getPaymentServiceURL());
        s.setCategories(dataService.getCategories());
        return s;
    }

    @PostMapping("/login")
    public User postLogin(@RequestBody @Validated LoginRequest login) {
        Optional<User> row = dataService.getUserByAccountName(login.getAccountName());
        if (!row.isPresent()) {
            throw new ApiException("アカウント名かパスワードが間違えています", HttpStatus.UNAUTHORIZED);
        }
        User u = row.get();
        String plaintext = login.getPassword();
        String hashed = new String(u.getHashedPassword(), StandardCharsets.UTF_8);
        if (!BCrypt.checkpw(plaintext, hashed)) {
            throw new ApiException("アカウント名かパスワードが間違えています", HttpStatus.UNAUTHORIZED);
        }
        sessionService.setUserId(u.getId());
        sessionService.setCsrfToken(secureRandomStr(20));
        return u;
    }

    // mux.HandleFunc(pat.Post("/register"), postRegister)
    // mux.HandleFunc(pat.Get("/reports.json"), getReports)

    private String secureRandomStr(int byteLength) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[byteLength];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder(byteLength * 2);
        for (int i = 0; i < byteLength; i++) {
            sb.append(String.format("%02x", bytes[i]));
        }
        return sb.toString();
    }

    private String getImageUrl(String imageName) {
        return "/upload/" + imageName;
    }

    private Optional<User> getUser() {
        return dataService.getUserById(sessionService.getUserId());
    }

    private Supplier<ApiException> notFound(String message) {
        return () -> new ApiException(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        ErrorResponse body = new ErrorResponse();
        String name = e.getName();
        String message = name.equals("user_id") ? "incorrect user id"
                : name.equals("item_id") ? "incorrect item id"
                        : name.equals("root_category_id") ? "incorrect category id" : name + " param errpr";
        body.setError(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleJsonMappingException(MethodArgumentNotValidException e) {
        ErrorResponse body = new ErrorResponse();
        body.setError("all parameters are required");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ErrorResponse body = new ErrorResponse();
        body.setError("json decode error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        ErrorResponse body = new ErrorResponse();
        body.setError("all parameters are required");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException e) {
        if (e.getStatus().is5xxServerError()) {
            Throwable cause = e.getCause();
            if (cause != null) {
                logger.info("A Server Error Occurred Caused by:", cause);
            } else {
                logger.info("A Server Error Occurred", e);
            }
        }
        ErrorResponse body = new ErrorResponse();
        body.setError(e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(body);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException e) {
        if (e.getStatus().is5xxServerError()) {
            Throwable cause = e.getCause();
            if (cause != null) {
                logger.info("A Server Error Occurred Caused by:", cause);
            } else {
                logger.info("A Server Error Occurred", e);
            }
        }
        ErrorResponse body = new ErrorResponse();
        body.setError(e.getReason());
        return ResponseEntity.status(e.getStatus()).body(body);
    }
}
