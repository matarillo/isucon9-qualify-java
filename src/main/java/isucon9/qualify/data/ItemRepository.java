package isucon9.qualify.data;

import java.time.LocalDateTime;

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
}
