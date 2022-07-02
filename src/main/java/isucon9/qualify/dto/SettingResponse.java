package isucon9.qualify.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class SettingResponse {
    /*
     * CSRFToken string `json:"csrf_token"`
     * PaymentServiceURL string `json:"payment_service_url"`
     * User *User `json:"user,omitempty"`
     * Categories []Category `json:"categories"`
     */

    private String csrfToken;
    private String paymentServiceUrl;
    @JsonInclude(Include.NON_NULL)
    private User user;
    @JsonInclude(Include.NON_NULL)
    private List<Category> categories;

    public String getCsrfToken() {
        return csrfToken;
    }

    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }

    public String getPaymentServiceUrl() {
        return paymentServiceUrl;
    }

    public void setPaymentServiceUrl(String paymentServiceUrl) {
        this.paymentServiceUrl = paymentServiceUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
