package isucon9.qualify.dto;

public class BuyResponse {
    /*
     * TransactionEvidenceID int64 `json:"transaction_evidence_id"`
     */

    private long transactionEvidenceId;

    public long getTransactionEvidenceId() {
        return transactionEvidenceId;
    }

    public void setTransactionEvidenceId(long transactionEvidenceId) {
        this.transactionEvidenceId = transactionEvidenceId;
    }
}
