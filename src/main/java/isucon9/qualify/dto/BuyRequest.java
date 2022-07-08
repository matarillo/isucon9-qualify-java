package isucon9.qualify.dto;

public class BuyRequest {
    /*
     * CSRFToken string `json:"csrf_token"`
     * ItemID int64 `json:"item_id"`
     * Token string `json:"token"`
     */

    private String csrfToken;
    private long itemId;
    private String token;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
