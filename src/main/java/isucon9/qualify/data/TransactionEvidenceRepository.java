package isucon9.qualify.data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import isucon9.qualify.dto.TransactionEvidence;

public interface TransactionEvidenceRepository extends CrudRepository<TransactionEvidence, Long> {

    public Optional<TransactionEvidence> findByItemId(long itemId);

    @Query("SELECT * FROM `transaction_evidences` WHERE `id` = :id FOR UPDATE")
    public Optional<TransactionEvidence> findByIdForUpdate(long id);

    @Query("SELECT * FROM `transaction_evidences` WHERE `item_id` = :itemId FOR UPDATE")
    public Optional<TransactionEvidence> findByItemIdForUpdate(long itemId);

    @Modifying
    @Query("UPDATE `transaction_evidences` SET `status` = :status, `updated_at` = :updatedAt WHERE `id` = :id")
    public boolean update(long id, String status, LocalDateTime updatedAt);

    @Query("SELECT * FROM `transaction_evidences` WHERE `id` > :id")
    public List<TransactionEvidence> findAfter(long id);
}
