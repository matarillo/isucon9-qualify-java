package isucon9.qualify.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Table("shippings")
public class ShippingToBuy implements Persistable<Long> {
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
    private byte[] imgBinary;

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

    @Override
    public Long getId() {
        return transactionEvidenceId;
    }

    @Override
    public boolean isNew() {
        return true;
    }
}
