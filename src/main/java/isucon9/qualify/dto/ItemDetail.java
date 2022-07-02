package isucon9.qualify.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ItemDetail {
    /*
     * ID int64 `json:"id"`
     * SellerID int64 `json:"seller_id"`
     * Seller *UserSimple `json:"seller"`
     * BuyerID int64 `json:"buyer_id,omitempty"`
     * Buyer *UserSimple `json:"buyer,omitempty"`
     * Status string `json:"status"`
     * Name string `json:"name"`
     * Price int `json:"price"`
     * Description string `json:"description"`
     * ImageURL string `json:"image_url"`
     * CategoryID int `json:"category_id"`
     * Category *Category `json:"category"`
     * TransactionEvidenceID int64 `json:"transaction_evidence_id,omitempty"`
     * TransactionEvidenceStatus string
     * `json:"transaction_evidence_status,omitempty"`
     * ShippingStatus string `json:"shipping_status,omitempty"`
     * CreatedAt int64 `json:"created_at"`
     */

    private long id;
    private long sellerId;
    private UserSimple seller;
    @JsonInclude(Include.NON_NULL)
    private long buyerId;
    @JsonInclude(Include.NON_NULL)
    private UserSimple buyer;
    private String status;
    private String name;
    private int price;
    private String description;
    private String imageUrl;
    private int categoryId;
    private Category category;
    @JsonInclude(Include.NON_NULL)
    private long transactionEvidenceId;
    @JsonInclude(Include.NON_NULL)
    private String transactionEvidenceStatus;
    @JsonInclude(Include.NON_NULL)
    private String shippingStatus;
    private long createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSellerId() {
        return sellerId;
    }

    public void setSellerId(long sellerId) {
        this.sellerId = sellerId;
    }

    public UserSimple getSeller() {
        return seller;
    }

    public void setSeller(UserSimple seller) {
        this.seller = seller;
    }

    public long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(long buyerId) {
        this.buyerId = buyerId;
    }

    public UserSimple getBuyer() {
        return buyer;
    }

    public void setBuyer(UserSimple buyer) {
        this.buyer = buyer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public long getTransactionEvidenceId() {
        return transactionEvidenceId;
    }

    public void setTransactionEvidenceId(long transactionEvidenceId) {
        this.transactionEvidenceId = transactionEvidenceId;
    }

    public String getTransactionEvidenceStatus() {
        return transactionEvidenceStatus;
    }

    public void setTransactionEvidenceStatus(String transactionEvidenceStatus) {
        this.transactionEvidenceStatus = transactionEvidenceStatus;
    }

    public String getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(String shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
