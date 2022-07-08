package isucon9.qualify.dto;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Table("shippings")
public class Shipping {
    /*
     * TransactionEvidenceID int64 `json:"transaction_evidence_id"
     * db:"transaction_evidence_id"`
     * Status string `json:"status" db:"status"`
     * ItemName string `json:"item_name" db:"item_name"`
     * ItemID int64 `json:"item_id" db:"item_id"`
     * ReserveID string `json:"reserve_id" db:"reserve_id"`
     * ReserveTime int64 `json:"reserve_time" db:"reserve_time"`
     * ToAddress string `json:"to_address" db:"to_address"`
     * ToName string `json:"to_name" db:"to_name"`
     * FromAddress string `json:"from_address" db:"from_address"`
     * FromName string `json:"from_name" db:"from_name"`
     * ImgBinary []byte `json:"-" db:"img_binary"`
     * CreatedAt time.Time `json:"-" db:"created_at"`
     * UpdatedAt time.Time `json:"-" db:"updated_at"`
     */

    @Id
    private long transactionEvidenceId;
    private String status;
    private String itemName;
    private long itemId;
    private String reserveId;
    private long reserveTime;
    private String toAddress;
    private String toName;
    private String fromAddress;
    private String fromName;
    @JsonIgnore
    private byte[] imgBinary;
    @JsonIgnore
    private LocalDateTime createdAt; // DEFAULT CURRENT_TIMESTAMP
    @JsonIgnore
    private LocalDateTime updatedAt; // DEFAULT CURRENT_TIMESTAMP

    public long getTransactionEvidenceId() {
        return transactionEvidenceId;
    }

    public void setTransactionEvidenceId(long transactionEvidenceId) {
        this.transactionEvidenceId = transactionEvidenceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getReserveId() {
        return reserveId;
    }

    public void setReserveId(String reserveId) {
        this.reserveId = reserveId;
    }

    public long getReserveTime() {
        return reserveTime;
    }

    public void setReserveTime(long reserveTime) {
        this.reserveTime = reserveTime;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public byte[] getImgBinary() {
        return imgBinary;
    }

    public void setImgBinary(byte[] imgBinary) {
        this.imgBinary = imgBinary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
