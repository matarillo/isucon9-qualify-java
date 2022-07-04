package isucon9.qualify.data;

import static isucon9.qualify.Const.DefaultPaymentServiceURL;
import static isucon9.qualify.Const.DefaultShipmentServiceURL;
import static isucon9.qualify.Const.ItemStatusCancel;
import static isucon9.qualify.Const.ItemStatusOnSale;
import static isucon9.qualify.Const.ItemStatusSoldOut;
import static isucon9.qualify.Const.ItemStatusStop;
import static isucon9.qualify.Const.ItemStatusTrading;
import static isucon9.qualify.Const.ItemsPerPage;
import static isucon9.qualify.Const.TransactionsPerPage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import isucon9.qualify.dto.Category;
import isucon9.qualify.dto.Item;
import isucon9.qualify.dto.Shipping;
import isucon9.qualify.dto.TransactionEvidence;
import isucon9.qualify.dto.User;
import isucon9.qualify.dto.UserSimple;

@Service
public class DataService {
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final TransactionEvidenceRepository transactionEvidenceRepository;
    private final ShippingRepository shippingRepository;

    public DataService(JdbcTemplate jdbcTemplate, UserRepository userRepository, CategoryRepository categoryRepository,
            ItemRepository itemRepository, TransactionEvidenceRepository transactionEvidenceRepository,
            ShippingRepository shippingRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
        this.transactionEvidenceRepository = transactionEvidenceRepository;
        this.shippingRepository = shippingRepository;
    }

    private Optional<String> getConfig(String name) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT * FROM configs WHERE name = ?", name);
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }
        Object o = list.get(0).getOrDefault("val", null);
        if (!(o instanceof String)) {
            return Optional.empty();
        }
        return Optional.of((String) o);
    }

    public String getPaymentServiceURL() {
        Optional<String> config = getConfig("payment_service_url");
        return config.orElse(DefaultPaymentServiceURL);
    }

    public String getShipmentServiceURL() {
        Optional<String> config = getConfig("shipment_service_url");
        return config.orElse(DefaultShipmentServiceURL);
    }

    public Optional<User> getUserById(long userId) {
        if (userId <= 0L) {
            return Optional.empty();
        }
        return userRepository.findById(userId);
    }

    public List<Category> getCategories() {
        Iterable<Category> iterable = categoryRepository.findAll();
        List<Category> categories = new ArrayList<Category>();
        iterable.forEach(categories::add);
        return categories;
    }

    public Optional<User> getUserByAccountName(String accountName) {
        return userRepository.findByAccountName(accountName);
    }

    public List<Item> getItems(LocalDateTime createdAt, long itemId) {
        Iterable<Item> iterable = itemRepository.findItems(ItemStatusOnSale, ItemStatusSoldOut, createdAt, itemId,
                ItemsPerPage + 1);
        List<Item> items = new ArrayList<Item>();
        iterable.forEach(items::add);
        return items;
    }

    public List<Item> getItems() {
        Iterable<Item> iterable = itemRepository.findItems(ItemStatusOnSale, ItemStatusSoldOut, ItemsPerPage + 1);
        List<Item> items = new ArrayList<Item>();
        iterable.forEach(items::add);
        return items;
    }

    public List<Item> getItemsForSale(long sellerId, LocalDateTime createdAt, long itemId) {
        Iterable<Item> iterable = itemRepository.findItems(sellerId, ItemStatusOnSale, ItemStatusTrading,
                ItemStatusSoldOut, createdAt, itemId, ItemsPerPage + 1);
        List<Item> items = new ArrayList<Item>();
        iterable.forEach(items::add);
        return items;
    }

    public List<Item> getItemsForSale(long sellerId) {
        Iterable<Item> iterable = itemRepository.findItems(sellerId, ItemStatusOnSale, ItemStatusTrading,
                ItemStatusSoldOut, ItemsPerPage + 1);
        List<Item> items = new ArrayList<Item>();
        iterable.forEach(items::add);
        return items;
    }

    public List<Item> getTransactionItems(long userId, LocalDateTime createdAt, long itemId) {
        Iterable<Item> iterable = itemRepository.findItems(userId, ItemStatusOnSale, ItemStatusTrading,
                ItemStatusSoldOut, ItemStatusCancel, ItemStatusStop, createdAt, itemId, TransactionsPerPage + 1);
        List<Item> items = new ArrayList<Item>();
        iterable.forEach(items::add);
        return items;
    }

    public List<Item> getTransactionItems(long userId) {
        Iterable<Item> iterable = itemRepository.findItems(userId, ItemStatusOnSale, ItemStatusTrading,
                ItemStatusSoldOut, ItemStatusCancel, ItemStatusStop, TransactionsPerPage + 1);
        List<Item> items = new ArrayList<Item>();
        iterable.forEach(items::add);
        return items;
    }

    public Optional<UserSimple> getUserSimpleById(long userId) {
        if (userId <= 0L) {
            return Optional.empty();
        }
        Optional<User> row = userRepository.findById(userId);
        if (row.isPresent()) {
            User user = row.get();
            UserSimple userSimple = new UserSimple();
            userSimple.setId(userId);
            userSimple.setAccountName(user.getAccountName());
            userSimple.setNumSellItems(user.getNumSellItems());
            return Optional.of(userSimple);
        } else {
            return Optional.empty();
        }
    }

    public Optional<Category> GetCategoryById(int categoryId) {
        if (categoryId <= 0) {
            return Optional.empty();
        }
        Optional<Category> row = categoryRepository.findById(categoryId);
        row.ifPresent(category -> {
            int parentCategoryId = category.getParentId();
            if (parentCategoryId > 0) {
                GetCategoryById(parentCategoryId)
                        .ifPresent(parentCategory -> category.setParentCategoryName(parentCategory.getCategoryName()));
            }
        });
        return row;
    }

    public Optional<Item> getItemById(long itemId) {
        if (itemId <= 0L) {
            return Optional.empty();
        }
        Optional<Item> row = itemRepository.findById(itemId);
        return row;
    }

    public Optional<TransactionEvidence> getTransactionEvidenceByItemId(long itemId) {
        if (itemId <= 0L) {
            return Optional.empty();
        }
        Optional<TransactionEvidence> row = transactionEvidenceRepository.findByItemId(itemId);
        return row;
    }

    public Optional<Shipping> getShippingById(long transactionEvidenceId) {
        if (transactionEvidenceId <= 0L) {
            return Optional.empty();
        }
        Optional<Shipping> row = shippingRepository.findById(transactionEvidenceId);
        return row;
    }

    public List<Integer> getCategoryIdsByRootCategoryId(int parentId) {
        return categoryRepository.findIdByParentId(parentId);
    }

    public List<Item> getItemsByCategoryIds(List<Integer> ids, LocalDateTime createdAt, long itemId) {
        return itemRepository.findItems(ItemStatusOnSale, ItemStatusTrading, ids, createdAt, itemId, ItemsPerPage + 1);
    }

    public List<Item> getItemsByCategoryIds(List<Integer> ids) {
        return itemRepository.findItems(ItemStatusOnSale, ItemStatusTrading, ids, ItemsPerPage + 1);
    }
}
