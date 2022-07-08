package isucon9.qualify.dto;

public class ApiPaymentServiceTokenRequest {
    /*
     * ShopID string `json:"shop_id"`
     * Token string `json:"token"`
     * APIKey string `json:"api_key"`
     * Price int `json:"price"
     */

    private String shopId;
    private String token;
    private String apiKey;
    private int price;

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
