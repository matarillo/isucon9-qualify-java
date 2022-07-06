package isucon9.qualify.dto;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Table("items")
public class Item {
    /*
     * ID int64 `json:"id" db:"id"`
     * SellerID int64 `json:"seller_id" db:"seller_id"`
     * BuyerID int64 `json:"buyer_id" db:"buyer_id"`
     * Status string `json:"status" db:"status"`
     * Name string `json:"name" db:"name"`
     * Price int `json:"price" db:"price"`
     * Description string `json:"description" db:"description"`
     * ImageName string `json:"image_name" db:"image_name"`
     * CategoryID int `json:"category_id" db:"category_id"`
     * CreatedAt time.Time `json:"-" db:"created_at"`
     * UpdatedAt time.Time `json:"-" db:"updated_at"`
     */

    @Id
    private Long id; // AUTO_INCREMENT
    private long sellerId;
    private long buyerId; // DEFAULT 0
    private String status;
    private String name;
    private int price;
    private String description;
    private String imageName;
    private int categoryId;
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

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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
