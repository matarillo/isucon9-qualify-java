package isucon9.qualify.data;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import isucon9.qualify.dto.User;

public interface UserRepository extends CrudRepository<User, Long> {

    public Optional<User> findByAccountName(String accountName);

    @Query("SELECT * FROM `users` WHERE `id` = :id FOR UPDATE")
    public Optional<User> findByIdForUpdate(long id);

    @Query("UPDATE `users` SET `num_sell_items` = :numSellItems, `last_bump` = :lastBump WHERE `id` = :id")
    public boolean update(long id, int numSellItems, LocalDateTime lastBump);

    @Query("UPDATE `users` SET `last_bump` = :lastBump WHERE `id` = :id")
    public boolean update(long id, LocalDateTime lastBump);
}
