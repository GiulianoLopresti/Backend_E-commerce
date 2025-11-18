package com.looprex.products.service;

import com.looprex.products.model.Category;
import com.looprex.products.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category createCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }

        if (categoryRepository.findByName(category.getName()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }

        return categoryRepository.save(category);
    }

    public Optional<Category> updateCategory(Long id, Category updatedCategory) {
        return categoryRepository.findById(id).map(existingCategory -> {
            if (updatedCategory.getName() != null && !updatedCategory.getName().trim().isEmpty()) {
                Optional<Category> categoryWithSameName = categoryRepository.findByName(updatedCategory.getName());
                if (categoryWithSameName.isPresent() && !categoryWithSameName.get().getCategoryId().equals(id)) {
                    throw new IllegalArgumentException("Ya existe otra categoría con ese nombre");
                }
                existingCategory.setName(updatedCategory.getName());
            }

            return categoryRepository.save(existingCategory);
        });
    }

    public boolean deleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existsByName(String name) {
        return categoryRepository.findByName(name).isPresent();
    }
}