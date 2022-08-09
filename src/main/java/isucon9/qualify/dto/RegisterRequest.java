package isucon9.qualify.dto;

public class RegisterRequest {
    /*
     * AccountName string `json:"account_name"`
     * Address string `json:"address"`
     * Password string `json:"password"`
     */

    private String accountName;
    private String address;
    private String password;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
