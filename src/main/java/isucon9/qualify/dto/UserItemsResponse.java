package isucon9.qualify.dto;

import java.util.List;

public class UserItemsResponse {
    /*
     * User *UserSimple `json:"user"`
     * HasNext bool `json:"has_next"`
     * Items []ItemSimple `json:"items"`
     */

    private UserSimple user;
    private boolean hasNext;
    private List<ItemSimple> items;

    public UserSimple getUser() {
        return user;
    }

    public void setUser(UserSimple user) {
        this.user = user;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public List<ItemSimple> getItems() {
        return items;
    }

    public void setItems(List<ItemSimple> items) {
        this.items = items;
    }
}
