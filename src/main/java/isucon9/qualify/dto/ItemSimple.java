package isucon9.qualify.dto;

public class ItemSimple {
    /*
     * ID int64 `json:"id"`
     * SellerID int64 `json:"seller_id"`
     * Seller *UserSimple `json:"seller"`
     * Status string `json:"status"`
     * Name string `json:"name"`
     * Price int `json:"price"`
     * ImageURL string `json:"image_url"`
     * CategoryID int `json:"category_id"`
     * Category *Category `json:"category"`
     * CreatedAt int64 `json:"created_at"`
     */

    private long id;
    private long sellerId;
    private UserSimple seller;
    private String status;
    private String name;
    private int price;
    private String imageUrl;
    private int categoryId;
    private Category category;
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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
