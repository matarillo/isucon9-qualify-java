package isucon9.qualify.data;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import isucon9.qualify.dto.TransactionEvidence;

public interface TransactionEvidenceRepository extends CrudRepository<TransactionEvidence, Long> {

    public Optional<TransactionEvidence> findByItemId(long itemId);

}
