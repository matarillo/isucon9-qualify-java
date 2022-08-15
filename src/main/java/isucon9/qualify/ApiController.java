package isucon9.qualify;

import static isucon9.qualify.Const.BcryptCost;
import static isucon9.qualify.Const.BumpChargeSeconds;
import static isucon9.qualify.Const.ItemMaxPrice;
import static isucon9.qualify.Const.ItemMinPrice;
import static isucon9.qualify.Const.ItemPriceErrMsg;
import static isucon9.qualify.Const.ItemStatusOnSale;
import static isucon9.qualify.Const.ItemStatusSoldOut;
import static isucon9.qualify.Const.ItemStatusTrading;
import static isucon9.qualify.Const.ItemsPerPage;
import static isucon9.qualify.Const.PaymentServiceIsucariApiKey;
import static isucon9.qualify.Const.PaymentServiceIsucariShopId;
import static isucon9.qualify.Const.ShippingsStatusDone;
import static isucon9.qualify.Const.ShippingsStatusInitial;
import static isucon9.qualify.Const.ShippingsStatusShipping;
import static isucon9.qualify.Const.ShippingsStatusWaitPickup;
import static isucon9.qualify.Const.TransactionEvidenceStatusDone;
import static isucon9.qualify.Const.TransactionEvidenceStatusWaitDone;
import static isucon9.qualify.Const.TransactionEvidenceStatusWaitShipping;
import static isucon9.qualify.Const.TransactionsPerPage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;
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
import isucon9.qualify.dto.ApiPaymentServiceTokenRequest;
import isucon9.qualify.dto.ApiPaymentServiceTokenResponse;
import isucon9.qualify.dto.ApiShipmentCreateRequest;
import isucon9.qualify.dto.ApiShipmentCreateResponse;
import isucon9.qualify.dto.ApiShipmentRequest;
import isucon9.qualify.dto.ApiShipmentStatusRequest;
import isucon9.qualify.dto.ApiShipmentStatusResponse;
import isucon9.qualify.dto.BumpRequest;
import isucon9.qualify.dto.BuyRequest;
import isucon9.qualify.dto.BuyResponse;
import isucon9.qualify.dto.Category;
import isucon9.qualify.dto.ErrorResponse;
import isucon9.qualify.dto.InitializeRequest;
import isucon9.qualify.dto.InitializeResponse;
import isucon9.qualify.dto.Item;
import isucon9.qualify.dto.ItemDetail;
import isucon9.qualify.dto.ItemEditRequest;
import isucon9.qualify.dto.ItemEditResponse;
import isucon9.qualify.dto.ItemSimple;
import isucon9.qualify.dto.ItemToSell;
import isucon9.qualify.dto.LoginRequest;
import isucon9.qualify.dto.NewItemsResponse;
import isucon9.qualify.dto.PostCompleteRequest;
import isucon9.qualify.dto.PostShipDoneRequest;
import isucon9.qualify.dto.PostShipRequest;
import isucon9.qualify.dto.PostShipResponse;
import isucon9.qualify.dto.RegisterRequest;
import isucon9.qualify.dto.SellResponse;
import isucon9.qualify.dto.SettingResponse;
import isucon9.qualify.dto.Shipping;
import isucon9.qualify.dto.ShippingToBuy;
import isucon9.qualify.dto.TransactionEvidence;
import isucon9.qualify.dto.TransactionEvidenceToBuy;
import isucon9.qualify.dto.TransactionsResponse;
import isucon9.qualify.dto.User;
import isucon9.qualify.dto.UserItemsResponse;
import isucon9.qualify.dto.UserSimple;
import isucon9.qualify.dto.UserToRegister;
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

    @PostMapping("/initialize")
    public InitializeResponse postInitialize(@RequestBody InitializeRequest request) {
        dataService.initializeDatabase();
        dataService.addConfig("payment_service_url", request.getPaymentServiceURL());
        dataService.addConfig("shipment_service_url", request.getShipmentServiceURL());
        InitializeResponse response = new InitializeResponse();
        response.setCampaign(0); // キャンペーン実施時には還元率の設定を返す。詳しくはマニュアルを参照のこと。
        response.setLanguage("Java"); // 実装言語を返す
        return response;
    }

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

    @PostMapping("/items/edit")
    public ItemEditResponse postItemEdit(@RequestBody ItemEditRequest request) {
        String csrfToken = request.getCsrfToken();
        long itemId = request.getItemId();
        throwIfInvalidCsrfToken(csrfToken);
        throwIfNotPositiveValue(itemId, "item_id param error");
        int price = request.getItemPrice();
        if (price < ItemMinPrice || price > ItemMaxPrice) {
            throw new ApiException(ItemPriceErrMsg, HttpStatus.BAD_REQUEST);
        }
        User seller = getUser().orElseThrow(notFound("user not found"));
        Item targetItem = dataService.getItemById(itemId).orElseThrow(notFound("item not found"));
        if (targetItem.getSellerId() != seller.getId()) {
            throw new ApiException("自分の商品以外は編集できません", HttpStatus.FORBIDDEN);
        }

        targetItem = tx.execute(status -> {
            Item editing = dataService.getItemByIdForUpdate(itemId).orElseThrow(notFound("item not found"));
            if (!editing.getStatus().equals(ItemStatusOnSale)) {
                throw new ApiException("販売中の商品以外編集できません", HttpStatus.FORBIDDEN);
            }
            LocalDateTime now = LocalDateTime.now();
            dataService.editItem(itemId, price, now);
            Item edited = dataService.getItemById(itemId).orElseThrow(internalServerError("db error"));
            return edited;
        });

        ItemEditResponse response = new ItemEditResponse();
        response.setItemId(targetItem.getId());
        response.setItemPrice(targetItem.getPrice());
        response.setItemCreatedAt(targetItem.getCreatedAt().toEpochSecond(ZoneOffset.UTC));
        response.setItemUpdatedAt(targetItem.getUpdatedAt().toEpochSecond(ZoneOffset.UTC));
        return response;
    }

    @PostMapping("/buy")
    public BuyResponse postBuy(@RequestBody BuyRequest request) {
        String csrfToken = request.getCsrfToken();
        long itemId = request.getItemId();
        throwIfInvalidCsrfToken(csrfToken);
        throwIfNotPositiveValue(itemId, "item_id param error");
        User buyer = getUser().orElseThrow(notFound("user not found"));

        ShippingToBuy newShipping = tx.execute(status -> {
            Item targetItem = dataService.getItemByIdForUpdate(itemId).orElseThrow(notFound("item not found"));
            if (!targetItem.getStatus().equals(ItemStatusOnSale)) {
                throw new ApiException("item is not for sale", HttpStatus.FORBIDDEN);
            }
            if (targetItem.getSellerId() == buyer.getId()) {
                throw new ApiException("自分の商品は買えません", HttpStatus.FORBIDDEN);
            }
            User seller = dataService.getUserByIdForUpdate(targetItem.getSellerId())
                    .orElseThrow(notFound("seller not found"));
            Category category = dataService.GetCategoryById(targetItem.getCategoryId())
                    .orElseThrow(internalServerError("category id error"));

            TransactionEvidenceToBuy evidenceInserting = new TransactionEvidenceToBuy();
            // id: AUTO INCREMENT
            evidenceInserting.setSellerId(seller.getId());
            evidenceInserting.setBuyerId(buyer.getId());
            evidenceInserting.setStatus(TransactionEvidenceStatusWaitShipping);
            evidenceInserting.setItemId(targetItem.getId());
            evidenceInserting.setItemName(targetItem.getName());
            evidenceInserting.setItemPrice(targetItem.getPrice());
            evidenceInserting.setItemDescription(targetItem.getDescription());
            evidenceInserting.setItemCategoryId(category.getId());
            evidenceInserting.setItemRootCategoryId(category.getParentId());
            // created_at: DEFAULT CURRENT_TIMESTAMP
            // updated_at: DEFAULT CURRENT_TIMESTAMP
            TransactionEvidenceToBuy evidenceInserted = dataService.saveTransactionEvidence(evidenceInserting);
            long transactionEvidenceId = evidenceInserted.getId();

            LocalDateTime now = LocalDateTime.now();
            dataService.buyItem(targetItem.getId(), buyer.getId(), now);

            ApiShipmentCreateRequest createRequest = new ApiShipmentCreateRequest();
            createRequest.setToAddress(buyer.getAddress());
            createRequest.setToName(buyer.getAccountName());
            createRequest.setFromAddress(seller.getAddress());
            createRequest.setFromName(seller.getAccountName());
            ApiShipmentCreateResponse createResponse = apiService.createShipment(dataService.getShipmentServiceURL(),
                    createRequest);

            ApiPaymentServiceTokenRequest tokenRequest = new ApiPaymentServiceTokenRequest();
            tokenRequest.setShopId(PaymentServiceIsucariShopId);
            tokenRequest.setToken(request.getToken());
            tokenRequest.setApiKey(PaymentServiceIsucariApiKey);
            tokenRequest.setPrice(targetItem.getPrice());
            ApiPaymentServiceTokenResponse tokenResponse = apiService
                    .getPaymentToken(dataService.getPaymentServiceURL(), tokenRequest);

            String tokenStatus = Optional.ofNullable(tokenResponse.getStatus()).orElse("");
            switch (tokenStatus) {
                case "ok":
                    break;
                case "invalid":
                    throw new ApiException("カード情報に誤りがあります", HttpStatus.BAD_REQUEST);
                case "fail":
                    throw new ApiException("カードの残高が足りません", HttpStatus.BAD_REQUEST);
                default:
                    throw new ApiException("想定外のエラー", HttpStatus.BAD_REQUEST);
            }

            ShippingToBuy shippingInserting = new ShippingToBuy();
            shippingInserting.setTransactionEvidenceId(transactionEvidenceId);
            shippingInserting.setStatus(ShippingsStatusInitial);
            shippingInserting.setItemName(targetItem.getName());
            shippingInserting.setItemId(targetItem.getId());
            shippingInserting.setReserveId(createResponse.getReserveId());
            shippingInserting.setReserveTime(createResponse.getReserveTime());
            shippingInserting.setToAddress(buyer.getAddress());
            shippingInserting.setToName(buyer.getAccountName());
            shippingInserting.setFromAddress(seller.getAddress());
            shippingInserting.setFromName(seller.getAccountName());
            shippingInserting.setImgBinary(new byte[0]);
            ShippingToBuy shippingInserted = dataService.saveShipping(shippingInserting);

            return shippingInserted;
        });

        BuyResponse response = new BuyResponse();
        response.setTransactionEvidenceId(newShipping.getTransactionEvidenceId());
        return response;
    }

    @PostMapping(value = "/sell", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SellResponse postSell(@RequestParam("csrf_token") String csrfToken, @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") int price,
            @RequestParam("category_id") int categoryId,
            @RequestParam("image") MultipartFile image,
            @Value("${isucon9.public}") Resource publicResource) {
        if (name.isEmpty() || description.isEmpty()) {
            throw new ApiException("all parameters are required", HttpStatus.BAD_REQUEST);
        }
        throwIfInvalidCsrfToken(csrfToken);
        throwIfNotPositiveValue(categoryId, "category id error");
        if (price < ItemMinPrice || price > ItemMaxPrice) {
            throw new ApiException(ItemPriceErrMsg, HttpStatus.BAD_REQUEST);
        }

        Category category = dataService.GetCategoryById(categoryId)
                .orElseThrow(() -> new ApiException("Incorrect category ID", HttpStatus.BAD_REQUEST));
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
            case "application/octet-stream":
                ext = "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
                if (ext.equals(".jpeg")) {
                    ext = ".jpg";
                }
                List<String> extList = Arrays.asList(".jpg", ".png", ".gif");
                if (extList.contains(ext)) {
                    break;
                }
                // FALLTHROUGH
            default:
                throw new ApiException("unsupported image format error", HttpStatus.BAD_REQUEST);
        }
        String imgName = secureRandomStr(16) + ext;
        try {
            Path target = publicResource.getFile().toPath().resolve("upload/" + imgName);
            image.transferTo(target);
        } catch (IOException e) {
            throw new ApiException("Saving image failed", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }

        ItemToSell newItem = tx.execute(status -> {
            User seller = dataService.getUserByIdForUpdate(user.getId()).orElseThrow(notFound("user not found"));
            ItemToSell inserting = new ItemToSell();
            // id: AUTO INCREMENT
            inserting.setSellerId(seller.getId());
            // buyer_id: DEFAULT 0
            inserting.setStatus(ItemStatusOnSale);
            inserting.setName(name);
            inserting.setPrice(price);
            inserting.setDescription(description);
            inserting.setImageName(imgName);
            inserting.setCategoryId(category.getId());
            // created_at: DEFAULT CURRENT_TIMESTAMP
            // updated_at: DEFAULT CURRENT_TIMESTAMP
            ItemToSell inserted = dataService.saveItem(inserting);
            dataService.updateUser(seller.getId(), seller.getNumSellItems() + 1, LocalDateTime.now());
            return inserted;
        });

        SellResponse response = new SellResponse();
        response.setId(newItem.getId());
        return response;
    }

    @PostMapping("/ship")
    public PostShipResponse postShip(@RequestBody PostShipRequest request) {
        String csrfToken = request.getCsrfToken();
        long itemId = request.getItemId();
        throwIfInvalidCsrfToken(csrfToken);
        throwIfNotPositiveValue(itemId, "item_id param error");
        User seller = getUser().orElseThrow(notFound("user not found"));
        TransactionEvidence transactionEvidence = dataService.getTransactionEvidenceByItemId(itemId)
                .orElseThrow(notFound("transaction_evidences not found"));
        if (transactionEvidence.getSellerId() != seller.getId()) {
            throw new ApiException("権限がありません", HttpStatus.FORBIDDEN);
        }

        String reserveId = tx.execute(status -> {
            Item item = dataService.getItemByIdForUpdate(itemId).orElseThrow(notFound("item not found"));
            if (!item.getStatus().equals(ItemStatusTrading)) {
                throw new ApiException("商品が取引中ではありません", HttpStatus.FORBIDDEN);
            }
            TransactionEvidence evidence = dataService.getTransactionEvidenceByIdForUpdate(transactionEvidence.getId())
                    .orElseThrow(notFound("transaction_evidences not found"));
            if (!evidence.getStatus().equals(TransactionEvidenceStatusWaitShipping)) {
                throw new ApiException("準備ができていません", HttpStatus.FORBIDDEN);
            }
            Shipping shipping = dataService.getShippingByIdForUpdate(evidence.getId())
                    .orElseThrow(notFound("shippings not found"));
            ApiShipmentRequest req = new ApiShipmentRequest();
            req.setReserveId(shipping.getReserveId());
            byte[] img = apiService.requestShipment(dataService.getShipmentServiceURL(), req);

            LocalDateTime now = LocalDateTime.now();
            dataService.requestShipping(evidence.getId(), img, now);
            return shipping.getReserveId();
        });

        PostShipResponse response = new PostShipResponse();
        response.setPath(String.format("/transactions/%d.png", transactionEvidence.getId()));
        response.setReserveId(reserveId);
        return response;
    }

    @PostMapping("/ship_done")
    public BuyResponse postShipDone(@RequestBody PostShipDoneRequest request) {
        String csrfToken = request.getCsrfToken();
        long itemId = request.getItemId();
        throwIfInvalidCsrfToken(csrfToken);
        throwIfNotPositiveValue(itemId, "item_id param error");
        User seller = getUser().orElseThrow(notFound("user not found"));
        TransactionEvidence transactionEvidence = dataService.getTransactionEvidenceByItemId(itemId)
                .orElseThrow(notFound("transaction_evidences not found"));
        if (transactionEvidence.getSellerId() != seller.getId()) {
            throw new ApiException("権限がありません", HttpStatus.FORBIDDEN);
        }

        long transactionEvidenceId = tx.execute(status -> {
            Item item = dataService.getItemByIdForUpdate(itemId).orElseThrow(notFound("item not found"));
            if (!item.getStatus().equals(ItemStatusTrading)) {
                throw new ApiException("商品が取引中ではありません", HttpStatus.FORBIDDEN);
            }
            TransactionEvidence evidence = dataService.getTransactionEvidenceByIdForUpdate(transactionEvidence.getId())
                    .orElseThrow(notFound("transaction_evidences not found"));
            if (!evidence.getStatus().equals(TransactionEvidenceStatusWaitShipping)) {
                throw new ApiException("準備ができていません", HttpStatus.FORBIDDEN);
            }
            Shipping shipping = dataService.getShippingByIdForUpdate(evidence.getId())
                    .orElseThrow(notFound("shippings not found"));
            ApiShipmentStatusRequest req = new ApiShipmentStatusRequest();
            req.setReserveId(shipping.getReserveId());
            ApiShipmentStatusResponse res = apiService.getShipmentStatus(dataService.getShipmentServiceURL(), req);
            if (!(res.getStatus().equals(ShippingsStatusShipping) || res.getStatus().equals(ShippingsStatusDone))) {
                throw new ApiException("shipment service側で配送中か配送完了になっていません", HttpStatus.FORBIDDEN);
            }

            LocalDateTime now = LocalDateTime.now();
            dataService.updateShippingStatus(evidence.getId(), res.getStatus(), now);
            dataService.updateTransactionEvidenceStatus(evidence.getId(), TransactionEvidenceStatusWaitDone, now);
            return evidence.getId();
        });

        BuyResponse response = new BuyResponse();
        response.setTransactionEvidenceId(transactionEvidenceId);
        return response;
    }

    @PostMapping("/complete")
    public BuyResponse postComplete(@RequestBody PostCompleteRequest request) {
        String csrfToken = request.getCsrfToken();
        long itemId = request.getItemId();
        throwIfInvalidCsrfToken(csrfToken);
        throwIfNotPositiveValue(itemId, "item_id param error");
        User buyer = getUser().orElseThrow(notFound("user not found"));
        TransactionEvidence transactionEvidence = dataService.getTransactionEvidenceByItemId(itemId)
                .orElseThrow(notFound("transaction_evidences not found"));
        if (transactionEvidence.getBuyerId() != buyer.getId()) {
            throw new ApiException("権限がありません", HttpStatus.FORBIDDEN);
        }

        long transactionEvidenceId = tx.execute(status -> {
            Item item = dataService.getItemByIdForUpdate(itemId).orElseThrow(notFound("item not found"));
            if (!item.getStatus().equals(ItemStatusTrading)) {
                throw new ApiException("商品が取引中ではありません", HttpStatus.FORBIDDEN);
            }
            TransactionEvidence evidence = dataService.getTransactionEvidenceByItemIdForUpdate(itemId)
                    .orElseThrow(notFound("transaction_evidences not found"));
            if (!evidence.getStatus().equals(TransactionEvidenceStatusWaitDone)) {
                throw new ApiException("準備ができていません", HttpStatus.FORBIDDEN);
            }
            Shipping shipping = dataService.getShippingByIdForUpdate(evidence.getId())
                    .orElseThrow(notFound("shippings not found"));
            ApiShipmentStatusRequest req = new ApiShipmentStatusRequest();
            req.setReserveId(shipping.getReserveId());
            ApiShipmentStatusResponse res = apiService.getShipmentStatus(dataService.getShipmentServiceURL(), req);
            if (!res.getStatus().equals(ShippingsStatusDone)) {
                throw new ApiException("shipment service側で配送完了になっていません", HttpStatus.BAD_REQUEST);
            }

            LocalDateTime now = LocalDateTime.now();
            dataService.updateShippingStatus(evidence.getId(), ShippingsStatusDone, now);
            dataService.updateTransactionEvidenceStatus(evidence.getId(), TransactionEvidenceStatusDone, now);
            dataService.updateItemStatus(itemId, ItemStatusSoldOut, now);
            return evidence.getId();
        });

        BuyResponse response = new BuyResponse();
        response.setTransactionEvidenceId(transactionEvidenceId);
        return response;
    }

    @GetMapping("/transactions/{transaction_evidence_id}.png")
    public ResponseEntity<byte[]> getQRCode(@PathVariable("transaction_evidence_id") long transactionEvidenceId) {
        throwIfNotPositiveValue(transactionEvidenceId, "incorrect transaction_evidence id");

        User seller = getUser().orElseThrow(notFound("user not found"));
        TransactionEvidence transactionEvidence = dataService.getTransactionEvidenceById(transactionEvidenceId)
                .orElseThrow(notFound("transaction_evidences not found"));
        if (transactionEvidence.getSellerId() != seller.getId()) {
            throw new ApiException("権限がありません", HttpStatus.FORBIDDEN);
        }
        Shipping shipping = dataService.getShippingById(transactionEvidence.getId())
                .orElseThrow(notFound("shippings not found"));
        String shippingStatus = Optional.ofNullable(shipping.getStatus()).orElse("");
        if (!shippingStatus.equals(ShippingsStatusWaitPickup) && !shippingStatus.equals(ShippingsStatusShipping)) {
            throw new ApiException("qrcode not available", HttpStatus.FORBIDDEN);
        }
        byte[] imgBinary = shipping.getImgBinary();
        if (imgBinary == null || imgBinary.length == 0) {
            throw new ApiException("empty qrcode image", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_PNG).body(imgBinary);
    }

    @PostMapping("/bump")
    public ItemEditResponse postBump(@RequestBody BumpRequest request) {
        String csrfToken = request.getCsrfToken();
        long itemId = request.getItemId();
        throwIfInvalidCsrfToken(csrfToken);
        throwIfNotPositiveValue(itemId, "item_id param error");
        User user = getUser().orElseThrow(notFound("user not found"));

        Item targetItem = tx.execute(status -> {
            Item bumping = dataService.getItemByIdForUpdate(itemId).orElseThrow(notFound("item not found"));
            if (bumping.getSellerId() != user.getId()) {
                throw new ApiException("自分の商品以外は編集できません", HttpStatus.FORBIDDEN);
            }
            User seller = dataService.getUserByIdForUpdate(user.getId()).orElseThrow(notFound("user not found"));

            LocalDateTime now = LocalDateTime.now();
            // last_bump + 3s > now
            LocalDateTime waitExpirationTime = seller.getLastBump().plus(BumpChargeSeconds);
            if (waitExpirationTime.isAfter(now)) {
                throw new ApiException("Bump not allowed", HttpStatus.FORBIDDEN);
            }

            dataService.bumpItem(itemId, now);
            dataService.updateUser(seller.getId(), now);
            Item bumped = dataService.getItemById(itemId).orElseThrow(internalServerError("db error"));
            return bumped;
        });

        ItemEditResponse response = new ItemEditResponse();
        response.setItemId(targetItem.getId());
        response.setItemPrice(targetItem.getPrice());
        response.setItemCreatedAt(targetItem.getCreatedAt().toEpochSecond(ZoneOffset.UTC));
        response.setItemUpdatedAt(targetItem.getUpdatedAt().toEpochSecond(ZoneOffset.UTC));
        return response;
    }

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
        Supplier<ApiException> unauthorizedSupplier = unauthorized("アカウント名かパスワードが間違えています");
        User user = dataService.getUserByAccountName(login.getAccountName()).orElseThrow(unauthorizedSupplier);
        String plaintext = login.getPassword();
        String hashed = new String(user.getHashedPassword(), StandardCharsets.UTF_8);
        if (!BCrypt.checkpw(plaintext, hashed)) {
            throw unauthorizedSupplier.get();
        }
        sessionService.setUserId(user.getId());
        sessionService.setCsrfToken(secureRandomStr(20));
        return user;
    }

    @PostMapping("/register")
    public User postRegister(@RequestBody RegisterRequest request) {
        String accountName = request.getAccountName();
        String address = request.getAddress();
        String password = request.getPassword();
        if (!(StringUtils.hasText(accountName) && StringUtils.hasText(password) && StringUtils.hasText(address))) {
            throw new ApiException("all parameters are required", HttpStatus.BAD_REQUEST);
        }
        String salt = BCrypt.gensalt(BcryptCost, new SecureRandom());
        byte[] hashedPassword = BCrypt.hashpw(password, salt).getBytes(StandardCharsets.UTF_8);

        UserToRegister inserting = new UserToRegister();
        // id: AUTO INCREMENT
        inserting.setAccountName(accountName);
        inserting.setAddress(address);
        inserting.setHashedPassword(hashedPassword);
        // num_sell_items: DEFAULT 0
        // last_bump: DEFAULT '2000-01-01 00:00:00'
        // created_at: DEFAULT CURRENT_TIMESTAMP
        UserToRegister inserted = null;
        try {
            inserted = dataService.saveUser(inserting);
        } catch (DbActionExecutionException e) {
            // Duplicate entry for 'account_name', etc.
            throw new ApiException("db error", HttpStatus.INTERNAL_SERVER_ERROR, e);
        }

        sessionService.setUserId(inserted.getId());
        sessionService.setCsrfToken(secureRandomStr(20));
        User user = new User();
        user.setId(inserted.getId());
        user.setAccountName(inserted.getAccountName());
        user.setAddress(inserted.getAddress());
        return user;
    }

    @GetMapping("/reports.json")
    public List<TransactionEvidence> getReports() {
        List<TransactionEvidence> response = dataService.getTransactionEvidencesForReports();
        return response;
    }

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

    private void throwIfNotPositiveValue(long id, String message) {
        if (id < 0L) {
            throw new ApiException(message, HttpStatus.BAD_REQUEST);
        }
    }

    private void throwIfInvalidCsrfToken(String csrfToken) {
        if (!sessionService.getCsrfToken().equals(csrfToken)) {
            throw new ApiException("csrf token error", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private Supplier<ApiException> unauthorized(String message) {
        return () -> new ApiException(message, HttpStatus.UNAUTHORIZED);
    }

    private Supplier<ApiException> notFound(String message) {
        return () -> new ApiException(message, HttpStatus.NOT_FOUND);
    }

    private Supplier<ApiException> internalServerError(String message) {
        return () -> new ApiException(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        ErrorResponse body = new ErrorResponse();
        String message;
        String name = e.getName();
        switch (name) {
            case "user_id":
                message = "incorrect user id";
                break;
            case "item_id":
                message = "incorrect item id";
                break;
            case "root_category_id":
                message = "incorrect category id";
                break;
            default:
                message = name + " param errpr";
                break;
        }
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
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
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
