package isucon9.qualify.dto;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Table("users")
public class User {
    /*
     * ID int64 `json:"id" db:"id"`
     * AccountName string `json:"account_name" db:"account_name"`
     * HashedPassword []byte `json:"-" db:"hashed_password"`
     * Address string `json:"address,omitempty" db:"address"`
     * NumSellItems int `json:"num_sell_items" db:"num_sell_items"`
     * LastBump time.Time `json:"-" db:"last_bump"`
     * CreatedAt time.Time `json:"-" db:"created_at"`
     */

    @Id
    private long id;
    private String accountName;
    @JsonIgnore
    private byte[] hashedPassword;
    private String address;
    private int numSellItems;
    @JsonIgnore
    private LocalDateTime lastBump;
    @JsonIgnore
    private LocalDateTime createdAt;

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

    public byte[] getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(byte[] hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getNumSellItems() {
        return numSellItems;
    }

    public void setNumSellItems(int numSellItems) {
        this.numSellItems = numSellItems;
    }

    public LocalDateTime getLastBump() {
        return lastBump;
    }

    public void setLastBump(LocalDateTime lastBump) {
        this.lastBump = lastBump;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
