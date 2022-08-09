package isucon9.qualify.dto;

public class PostShipDoneRequest {
    /*
     * CSRFToken string `json:"csrf_token"`
     * ItemID    int64  `json:"item_id"`
     */

    private String csrfToken;
    private long itemId;

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
}
