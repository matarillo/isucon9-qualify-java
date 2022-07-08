package isucon9.qualify.dto;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Table("transaction_evidences")
public class TransactionEvidence {
    /*
     * ID int64 `json:"id" db:"id"`
     * SellerID int64 `json:"seller_id" db:"seller_id"`
     * BuyerID int64 `json:"buyer_id" db:"buyer_id"`
     * Status string `json:"status" db:"status"`
     * ItemID int64 `json:"item_id" db:"item_id"`
     * ItemName string `json:"item_name" db:"item_name"`
     * ItemPrice int `json:"item_price" db:"item_price"`
     * ItemDescription string `json:"item_description" db:"item_description"`
     * ItemCategoryID int `json:"item_category_id" db:"item_category_id"`
     * ItemRootCategoryID int `json:"item_root_category_id"
     * db:"item_root_category_id"`
     * CreatedAt time.Time `json:"-" db:"created_at"`
     * UpdatedAt time.Time `json:"-" db:"updated_at"`
     */

    @Id
    private Long id; // AUTO_INCREMENT
    private long sellerId;
    private long buyerId;
    private String status;
    private long itemId;
    private String itemName;
    private int itemPrice;
    private String itemDescription;
    private int itemCategoryId;
    private int itemRootCategoryId;
    @JsonIgnore
    private LocalDateTime createdAt; // DEFAULT CURRENT_TIMESTAMP
    @JsonIgnore
    private LocalDateTime updatedAt; // DEFAULT CURRENT_TIMESTAMP

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getSellerId() {
        return sellerId;
    }

    public void setSellerId(long sellerId) {
        this.sellerId = sellerId;
    }

    public long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(long buyerId) {
        this.buyerId = buyerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public int getItemCategoryId() {
        return itemCategoryId;
    }

    public void setItemCategoryId(int itemCategoryId) {
        this.itemCategoryId = itemCategoryId;
    }

    public int getItemRootCategoryId() {
        return itemRootCategoryId;
    }

    public void setItemRootCategoryId(int itemRootCategoryId) {
        this.itemRootCategoryId = itemRootCategoryId;
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
