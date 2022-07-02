package isucon9.qualify.dto;

import java.util.List;

public class TransactionsResponse {
    /*
     * HasNext bool `json:"has_next"`
     * Items []ItemDetail `json:"items"`
     */

    private boolean hasNext;
    private List<ItemDetail> items;

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public List<ItemDetail> getItems() {
        return items;
    }

    public void setItems(List<ItemDetail> items) {
        this.items = items;
    }
}
