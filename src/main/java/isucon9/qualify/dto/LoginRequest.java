package isucon9.qualify.dto;

import javax.validation.constraints.NotEmpty;

public class LoginRequest {
    /*
     * AccountName string `json:"account_name"`
     * Password string `json:"password"`
     */

    @NotEmpty
    private String accountName;
    @NotEmpty
    private String password;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
