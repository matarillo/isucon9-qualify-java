package isucon9.qualify.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Table("categories")
public class Category {
    /*
     * ID int `json:"id" db:"id"`
     * ParentID int `json:"parent_id" db:"parent_id"`
     * CategoryName string `json:"category_name" db:"category_name"`
     * ParentCategoryName string `json:"parent_category_name,omitempty" db:"-"`
     */

    @Id
    private int id;
    private int parentId;
    private String categoryName;
    @JsonInclude(Include.NON_NULL)
    @Transient
    private String parentCategoryName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getParentCategoryName() {
        return parentCategoryName;
    }

    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }
}
