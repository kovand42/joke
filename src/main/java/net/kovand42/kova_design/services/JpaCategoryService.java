package net.kovand42.kova_design.services;

import net.kovand42.kova_design.entities.Category;
import net.kovand42.kova_design.repositories.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly=true,isolation= Isolation.READ_COMMITTED)
public class JpaCategoryService implements CategoryService {
    private final CategoryRepository categoryRepository;

    public JpaCategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Optional<Category> findById(long categoryId) {
        return categoryRepository.findById(categoryId);
    }

    @Override
    public Optional<Category> findByCategoryName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void delete(Category category) {
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void create(Category category) {
        categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void update(Category category) {
        categoryRepository.save(category);
    }
}
