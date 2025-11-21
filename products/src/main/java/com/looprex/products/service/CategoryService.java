package com.looprex.products.service;

import com.looprex.products.model.Category;
import com.looprex.products.model.Product;
import com.looprex.products.repository.CategoryRepository;
import com.looprex.products.repository.ProductRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    
    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
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

    public void deleteCategory(Long id) { 
        // Verificar que existe
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Categoría no encontrada");
        }
        
        // Verificar que no tenga productos asociados
        List<Product> products = productRepository.findByCategoryId(id);
        if (!products.isEmpty()) {
            throw new IllegalStateException(
                "No se puede eliminar la categoría porque tiene " + 
                products.size() + " producto(s) asociado(s). " +
                "Elimina los productos primero."
            );
        }
        // Si no tiene productos, eliminar
        categoryRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return categoryRepository.findByName(name).isPresent();
    }
}