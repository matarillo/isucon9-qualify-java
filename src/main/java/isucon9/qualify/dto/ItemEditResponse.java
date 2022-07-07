package isucon9.qualify.dto;

public class ItemEditResponse {
    /*
     * ItemID        int64 `json:"item_id"`
     * ItemPrice     int   `json:"item_price"`
     * ItemCreatedAt int64 `json:"item_created_at"`
     * ItemUpdatedAt int64 `json:"item_updated_at"`
     */

    private long itemId;
    private int itemPrice;
    private long itemCreatedAt;
    private long itemUpdatedAt;

    public long getItemId() {
        return itemId;
    }
    public void setItemId(long itemId) {
        this.itemId = itemId;
    }
    public int getItemPrice() {
        return itemPrice;
    }
    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }
    public long getItemCreatedAt() {
        return itemCreatedAt;
    }
    public void setItemCreatedAt(long itemCreatedAt) {
        this.itemCreatedAt = itemCreatedAt;
    }
    public long getItemUpdatedAt() {
        return itemUpdatedAt;
    }
    public void setItemUpdatedAt(long itemUpdatedAt) {
        this.itemUpdatedAt = itemUpdatedAt;
    }
}
