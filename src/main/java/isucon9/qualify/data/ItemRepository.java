package isucon9.qualify.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import isucon9.qualify.dto.Item;

public interface ItemRepository extends CrudRepository<Item, Long> {

    @Query("SELECT * FROM `items` WHERE `status` IN (:status0, :status1) AND (`created_at` < :createdAt OR (`created_at` <= :createdAt AND `id` < :itemId)) ORDER BY `created_at` DESC, `id` DESC LIMIT :itemsPerPage")
    public Iterable<Item> findItems(String status0, String status1, LocalDateTime createdAt, long itemId, int itemsPerPage);

    @Query("SELECT * FROM `items` WHERE `status` IN (:status0, :status1) ORDER BY `created_at` DESC, `id` DESC LIMIT :itemsPerPage")
    public Iterable<Item> findItems(String status0, String status1, int itemsPerPage);

    @Query("SELECT * FROM `items` WHERE `seller_id` = :sellerId AND `status` IN (:status0, :status1, :status2) AND (`created_at` < :createdAt OR (`created_at` <= :createdAt AND `id` < :itemId)) ORDER BY `created_at` DESC, `id` DESC LIMIT :itemsPerPage")
    public Iterable<Item> findItems(long sellerId, String status0, String status1, String status2, LocalDateTime createdAt, long itemId, int itemsPerPage);

    @Query("SELECT * FROM `items` WHERE `seller_id` = :sellerId AND `status` IN (:status0, :status1, :status2) ORDER BY `created_at` DESC, `id` DESC LIMIT :itemsPerPage")
    public Iterable<Item> findItems(long sellerId, String status0, String status1, String status2, int itemsPerPage);

    @Query("SELECT * FROM `items` WHERE (`seller_id` = :userId OR `buyer_id` = :userId) AND `status` IN (:status0, :status1, :status2, :status3, :status4) AND (`created_at` < :createdAt  OR (`created_at` <= :createdAt AND `id` < :itemId)) ORDER BY `created_at` DESC, `id` DESC LIMIT :transactionsPerPage")
    public Iterable<Item> findItems(long userId, String status0, String status1, String status2, String status3, String status4, LocalDateTime createdAt, long itemId, int transactionsPerPage);

    @Query("SELECT * FROM `items` WHERE (`seller_id` = :userId OR `buyer_id` = :userId) AND `status` IN (:status0, :status1, :status2, :status3, :status4) ORDER BY `created_at` DESC, `id` DESC LIMIT :transactionsPerPage")
    public Iterable<Item> findItems(long userId, String status0, String status1, String status2, String status3, String status4, int transactionsPerPage);

    @Query("SELECT * FROM `items` WHERE `status` IN (:status0, :status1) AND category_id IN (:ids) AND (`created_at` < :createdAt OR (`created_at` <= :createdAt AND `id` < :itemId)) ORDER BY `created_at` DESC, `id` DESC LIMIT :itemsPerPage")
    public List<Item> findItems(String status0, String status1, List<Integer> ids, LocalDateTime createdAt, long itemId, int itemsPerPage);

    @Query("SELECT * FROM `items` WHERE `status` IN (:status0, :status1) AND category_id IN (:ids) ORDER BY `created_at` DESC, `id` DESC LIMIT :itemsPerPage")
    public List<Item> findItems(String status0, String status1, List<Integer> ids, int itemsPerPage);

    @Query("SELECT * FROM `items` WHERE `id` = :id FOR UPDATE")
    public Optional<Item> findByIdForUpdate(long id);

    @Query("UPDATE `items` SET `created_at` = :createdAt, `updated_at` = :updatedAt WHERE id = :id")
    public boolean update(long id, LocalDateTime createdAt, LocalDateTime updatedAt);

    @Query("UPDATE `items` SET `buyer_id` = :buyerId, `status` = :status, `updated_at` = ::updatedAt WHERE `id` = :id")
    public boolean update(long id, long buyerId, String status, LocalDateTime updatedAt);
}
