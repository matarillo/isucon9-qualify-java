package isucon9.qualify.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class NewItemsResponse {

    /*
     * RootCategoryID int `json:"root_category_id,omitempty"`
     * RootCategoryName string `json:"root_category_name,omitempty"`
     * HasNext bool `json:"has_next"`
     * Items []ItemSimple `json:"items"`
     */

    @JsonInclude(Include.NON_NULL)
    private int rootCategoryId;
    @JsonInclude(Include.NON_NULL)
    private String rootCategoryName;
    private boolean hasNext;
    private List<ItemSimple> items;

    public int getRootCategoryId() {
        return rootCategoryId;
    }

    public void setRootCategoryId(int rootCategoryId) {
        this.rootCategoryId = rootCategoryId;
    }

    public String getRootCategoryName() {
        return rootCategoryName;
    }

    public void setRootCategoryName(String rootCategoryName) {
        this.rootCategoryName = rootCategoryName;
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
