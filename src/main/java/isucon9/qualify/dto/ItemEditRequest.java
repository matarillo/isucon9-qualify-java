package isucon9.qualify.dto;

public class ItemEditRequest {
    /*
     * CSRFToken string `json:"csrf_token"`
     * ItemID int64 `json:"item_id"`
     * ItemPrice int `json:"item_price"`
     */

    private String csrfToken;
    private long itemId;
    private int itemPrice;

    public String getCsrfToken() {
        return csrfToken;
    }

    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }

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
}
