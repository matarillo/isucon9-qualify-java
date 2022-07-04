package isucon9.qualify.data;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import isucon9.qualify.dto.Category;

public interface CategoryRepository extends CrudRepository<Category, Integer> {

    @Query("SELECT id FROM `categories` WHERE parent_id = :parentId")
    public List<Integer> findIdByParentId(int parentId);

}
