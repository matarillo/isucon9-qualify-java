package isucon9.qualify.data;

import org.springframework.data.repository.CrudRepository;

import isucon9.qualify.dto.Category;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
}
