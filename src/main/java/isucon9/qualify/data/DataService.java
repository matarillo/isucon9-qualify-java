package isucon9.qualify.data;

import static isucon9.qualify.Const.DefaultPaymentServiceURL;
import static isucon9.qualify.Const.DefaultShipmentServiceURL;
import static isucon9.qualify.Const.ItemStatusCancel;
import static isucon9.qualify.Const.ItemStatusOnSale;
import static isucon9.qualify.Const.ItemStatusSoldOut;
import static isucon9.qualify.Const.ItemStatusStop;
import static isucon9.qualify.Const.ItemStatusTrading;
import static isucon9.qualify.Const.ItemsPerPage;
import static isucon9.qualify.Const.ShippingsStatusWaitPickup;
import static isucon9.qualify.Const.TransactionsPerPage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import isucon9.qualify.dto.Category;
import isucon9.qualify.dto.Item;
import isucon9.qualify.dto.ItemToSell;
import isucon9.qualify.dto.Shipping;
import isucon9.qualify.dto.ShippingToBuy;
import isucon9.qualify.dto.TransactionEvidence;
import isucon9.qualify.dto.TransactionEvidenceToBuy;
import isucon9.qualify.dto.User;
import isucon9.qualify.dto.UserSimple;
import isucon9.qualify.dto.UserToRegister;

@Service
public class DataService {
    private final SqlInitializationProperties properties;
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final UserToRegisterRepository userToRegisterRepository;
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final ItemToSellRepository itemToSellRepository;
    private final TransactionEvidenceRepository transactionEvidenceRepository;
    private final TransactionEvidenceToBuyRepository transactionEvidenceToBuyRepository;
    private final ShippingRepository shippingRepository;
    private final ShippingToBuyRepository shippingToBuyRepository;

    public DataService(SqlInitializationProperties properties, DataSource dataSource, JdbcTemplate jdbcTemplate,
            UserRepository userRepository, UserToRegisterRepository userToRegisterRepository,
            CategoryRepository categoryRepository, ItemRepository itemRepository,
            ItemToSellRepository itemToSellRepository,
            TransactionEvidenceRepository transactionEvidenceRepository,
            TransactionEvidenceToBuyRepository transactionEvidenceToBuyRepository,
            ShippingRepository shippingRepository, ShippingToBuyRepository shippingToBuyRepository) {
        this.properties = properties;
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
        this.userToRegisterRepository = userToRegisterRepository;
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
        this.itemToSellRepository = itemToSellRepository;
        this.transactionEvidenceRepository = transactionEvidenceRepository;
        this.transactionEvidenceToBuyRepository = transactionEvidenceToBuyRepository;
        this.shippingRepository = shippingRepository;
        this.shippingToBuyRepository = shippingToBuyRepository;
    }

    public void initializeDatabase() {
        properties.setMode(DatabaseInitializationMode.ALWAYS);
        SqlDataSourceScriptDatabaseInitializer initializer = new SqlDataSourceScriptDatabaseInitializer(dataSource,
                properties);
        initializer.initializeDatabase();
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

    public int addConfig(String name, String value) {
        int rowCount = jdbcTemplate.update(
                "INSERT INTO `configs` (`name`, `val`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `val` = VALUES(`val`)",
                name, value);
        return rowCount;
    }

    public Optional<User> getUserById(long userId) {
        if (userId <= 0L) {
            return Optional.empty();
        }
        return userRepository.findById(userId);
    }

    public Optional<User> getUserByIdForUpdate(long userId) {
        if (userId <= 0L) {
            return Optional.empty();
        }
        return userRepository.findByIdForUpdate(userId);
    }

    public boolean updateUser(long id, int numSellItems, LocalDateTime lastBump) {
        return userRepository.update(id, numSellItems, lastBump);
    }

    public boolean updateUser(long id, LocalDateTime lastBump) {
        return userRepository.update(id, lastBump);
    }

    public UserToRegister saveUser(UserToRegister newUser) {
        return userToRegisterRepository.save(newUser);
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
        List<Item> items = itemRepository.findItems(ItemStatusOnSale, ItemStatusSoldOut, createdAt, itemId,
                ItemsPerPage + 1);
        return items;
    }

    public List<Item> getItems() {
        List<Item> items = itemRepository.findItems(ItemStatusOnSale, ItemStatusSoldOut, ItemsPerPage + 1);
        return items;
    }

    public List<Item> getItemsForSale(long sellerId, LocalDateTime createdAt, long itemId) {
        List<Item> items = itemRepository.findItems(sellerId, ItemStatusOnSale, ItemStatusTrading,
                ItemStatusSoldOut, createdAt, itemId, ItemsPerPage + 1);
        return items;
    }

    public List<Item> getItemsForSale(long sellerId) {
        List<Item> items = itemRepository.findItems(sellerId, ItemStatusOnSale, ItemStatusTrading,
                ItemStatusSoldOut, ItemsPerPage + 1);
        return items;
    }

    public List<Item> getTransactionItems(long userId, LocalDateTime createdAt, long itemId) {
        List<Item> items = itemRepository.findItems(userId, ItemStatusOnSale, ItemStatusTrading,
                ItemStatusSoldOut, ItemStatusCancel, ItemStatusStop, createdAt, itemId, TransactionsPerPage + 1);
        return items;
    }

    public List<Item> getTransactionItems(long userId) {
        List<Item> items = itemRepository.findItems(userId, ItemStatusOnSale, ItemStatusTrading,
                ItemStatusSoldOut, ItemStatusCancel, ItemStatusStop, TransactionsPerPage + 1);
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

    public Optional<Item> getItemByIdForUpdate(long itemId) {
        if (itemId <= 0L) {
            return Optional.empty();
        }
        Optional<Item> row = itemRepository.findByIdForUpdate(itemId);
        return row;
    }

    public boolean bumpItem(long itemId, LocalDateTime dateTime) {
        return itemRepository.update(itemId, dateTime, dateTime);
    }

    public boolean buyItem(long itemId, long buyerId, LocalDateTime updatedAt) {
        return itemRepository.update(itemId, buyerId, ItemStatusTrading, updatedAt);
    }

    public boolean editItem(long itemId, int price, LocalDateTime updatedAt) {
        return itemRepository.update(itemId, price, updatedAt);
    }

    public boolean updateItemStatus(long itemId, String status, LocalDateTime updatedAt) {
        return itemRepository.update(itemId, status, updatedAt);
    }

    public ItemToSell saveItem(ItemToSell newItem) {
        return itemToSellRepository.save(newItem);
    }

    public Optional<TransactionEvidence> getTransactionEvidenceById(long transactionEvidenceId) {
        if (transactionEvidenceId <= 0L) {
            return Optional.empty();
        }
        Optional<TransactionEvidence> row = transactionEvidenceRepository.findById(transactionEvidenceId);
        return row;
    }

    public Optional<TransactionEvidence> getTransactionEvidenceByIdForUpdate(long transactionEvidenceId) {
        if (transactionEvidenceId <= 0L) {
            return Optional.empty();
        }
        Optional<TransactionEvidence> row = transactionEvidenceRepository.findByIdForUpdate(transactionEvidenceId);
        return row;
    }

    public Optional<TransactionEvidence> getTransactionEvidenceByItemId(long itemId) {
        if (itemId <= 0L) {
            return Optional.empty();
        }
        Optional<TransactionEvidence> row = transactionEvidenceRepository.findByItemId(itemId);
        return row;
    }

    public Optional<TransactionEvidence> getTransactionEvidenceByItemIdForUpdate(long itemId) {
        if (itemId <= 0L) {
            return Optional.empty();
        }
        Optional<TransactionEvidence> row = transactionEvidenceRepository.findByItemIdForUpdate(itemId);
        return row;
    }

    public List<TransactionEvidence> getTransactionEvidencesForReports() {
        return transactionEvidenceRepository.findAfter(15007); // magic number 15007
    }

    public boolean updateTransactionEvidenceStatus(long transactionEvidenceId, String status, LocalDateTime updatedAt) {
        return transactionEvidenceRepository.update(transactionEvidenceId, status, updatedAt);
    }

    public TransactionEvidenceToBuy saveTransactionEvidence(TransactionEvidenceToBuy newEvidence) {
        return transactionEvidenceToBuyRepository.save(newEvidence);
    }

    public Optional<Shipping> getShippingById(long transactionEvidenceId) {
        if (transactionEvidenceId <= 0L) {
            return Optional.empty();
        }
        Optional<Shipping> row = shippingRepository.findById(transactionEvidenceId);
        return row;
    }

    public Optional<Shipping> getShippingByIdForUpdate(long transactionEvidenceId) {
        if (transactionEvidenceId <= 0L) {
            return Optional.empty();
        }
        Optional<Shipping> row = shippingRepository.findByIdForUpdate(transactionEvidenceId);
        return row;
    }

    public ShippingToBuy saveShipping(ShippingToBuy newShipping) {
        return shippingToBuyRepository.save(newShipping);
    }

    public boolean requestShipping(long transactionEvidenceId, byte[] imgBinary, LocalDateTime updatedAt) {
        return shippingRepository.update(transactionEvidenceId, ShippingsStatusWaitPickup, imgBinary, updatedAt);
    }

    public boolean updateShippingStatus(long transactionEvidenceId, String status, LocalDateTime updatedAt) {
        return shippingRepository.update(transactionEvidenceId, status, updatedAt);
    }

    public List<Integer> getCategoryIdsByRootCategoryId(int parentId) {
        return categoryRepository.findIdByParentId(parentId);
    }

    public List<Item> getItemsByCategoryIds(List<Integer> ids, LocalDateTime createdAt, long itemId) {
        return itemRepository.findItems(ItemStatusOnSale, ItemStatusSoldOut, ids, createdAt, itemId, ItemsPerPage + 1);
    }

    public List<Item> getItemsByCategoryIds(List<Integer> ids) {
        return itemRepository.findItems(ItemStatusOnSale, ItemStatusSoldOut, ids, ItemsPerPage + 1);
    }
}
