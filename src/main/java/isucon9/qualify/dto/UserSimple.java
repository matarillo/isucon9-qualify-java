package isucon9.qualify.dto;

public class UserSimple {

    /*
     * ID int64 `json:"id"`
     * AccountName string `json:"account_name"`
     * NumSellItems int `json:"num_sell_items"`
     */

    private long id;
    private String accountName;
    private int numSellItems;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getNumSellItems() {
        return numSellItems;
    }

    public void setNumSellItems(int numSellItems) {
        this.numSellItems = numSellItems;
    }
}
