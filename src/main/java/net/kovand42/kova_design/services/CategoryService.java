package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Optional<Category> findById(long categoryId);
    Optional<Category> findByCategoryName(String categoryName);
    List<Category> findAll();
    void delete(Category category);
    void create(Category category);
    void update(Category category);
}
