package isucon9.qualify.data;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import isucon9.qualify.dto.Shipping;

public interface ShippingRepository extends CrudRepository<Shipping, Long> {

    @Query("SELECT * FROM `shippings` WHERE `transaction_evidence_id` = :transactionEvidenceId FOR UPDATE")
    public Optional<Shipping> findByIdForUpdate(long transactionEvidenceId);

    @Query("UPDATE `shippings` SET `status` = :status, `img_binary` = :imgBinary, `updated_at` = :updatedAt WHERE `transaction_evidence_id` = :transactionEvidenceId")
    public boolean update(long transactionEvidenceId, String status, byte[] imgBinary, LocalDateTime updatedAt);

}
