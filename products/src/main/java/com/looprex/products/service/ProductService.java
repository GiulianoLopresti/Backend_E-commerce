package com.looprex.products.service;

import com.looprex.products.model.Category;
import com.looprex.products.model.Product;
import com.looprex.products.model.Status;
import com.looprex.products.repository.CategoryRepository;
import com.looprex.products.repository.ProductRepository;
import com.looprex.products.repository.StatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StatusRepository statusRepository;

    private static final String DOESNT_EXIST = " no existe";

    public ProductService(ProductRepository productRepository,
                         CategoryRepository categoryRepository,
                         StatusRepository statusRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.statusRepository = statusRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> searchProducts(String query) {
        return productRepository.searchByName(query);
    }

    public List<Product> getProductsByStatus(Long statusId) {
        return productRepository.findByStatusId(statusId);
    }

    public Product createProduct(Product product) {
        // Validaciones básicas
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }

        if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del producto no puede estar vacía");
        }

        if (product.getPrice() == null || product.getPrice() < 0) {
            throw new IllegalArgumentException("El precio debe ser mayor o igual a 0");
        }

        if (product.getStock() == null || product.getStock() < 0) {
            throw new IllegalArgumentException("El stock debe ser mayor o igual a 0");
        }

        if (product.getCategoryId() == null) {
            throw new IllegalArgumentException("El producto debe tener una categoría");
        }

        if (product.getStatusId() == null) {
            throw new IllegalArgumentException("El producto debe tener un estado");
        }

        // Cargar categoría completa
        Category category = categoryRepository.findById(product.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("La categoría con ID " + product.getCategoryId() + DOESNT_EXIST));

        // Cargar estado completo
        Status status = statusRepository.findById(product.getStatusId())
                .orElseThrow(() -> new IllegalArgumentException("El estado con ID " + product.getStatusId() + DOESNT_EXIST));
        product.setCategory(category);
        product.setStatus(status);

        // Guardar y recargar
        Product saved = productRepository.save(product);
        return productRepository.findById(saved.getProductId()).orElse(saved);
    }

    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id).map(existingProduct -> {
            updateBasicFields(updatedProduct, existingProduct);
            updateCategoryIfProvided(updatedProduct, existingProduct);
            updateStatusIfProvided(updatedProduct, existingProduct);

            Product saved = productRepository.save(existingProduct);
            return productRepository.findById(saved.getProductId()).orElse(saved);
        });
    }

    private void updateBasicFields(Product updatedProduct, Product existingProduct) {
        if (updatedProduct.getName() != null && !updatedProduct.getName().trim().isEmpty()) {
            existingProduct.setName(updatedProduct.getName());
        }

        if (updatedProduct.getDescription() != null && !updatedProduct.getDescription().trim().isEmpty()) {
            existingProduct.setDescription(updatedProduct.getDescription());
        }

        updatePriceIfProvided(updatedProduct, existingProduct);
        updateStockIfProvided(updatedProduct, existingProduct);

        if (updatedProduct.getProductPhoto() != null) {
            existingProduct.setProductPhoto(updatedProduct.getProductPhoto());
        }
    }

    private void updatePriceIfProvided(Product updatedProduct, Product existingProduct) {
        if (updatedProduct.getPrice() != null) {
            if (updatedProduct.getPrice() < 0) {
                throw new IllegalArgumentException("El precio no puede ser negativo");
            }
            existingProduct.setPrice(updatedProduct.getPrice());
        }
    }

    private void updateStockIfProvided(Product updatedProduct, Product existingProduct) {
        if (updatedProduct.getStock() != null) {
            if (updatedProduct.getStock() < 0) {
                throw new IllegalArgumentException("El stock no puede ser negativo");
            }
            existingProduct.setStock(updatedProduct.getStock());
        }
    }

    private void updateCategoryIfProvided(Product updatedProduct, Product existingProduct) {
        if (updatedProduct.getCategoryId() != null) {
            Category category = categoryRepository.findById(updatedProduct.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("La categoría con ID " + updatedProduct.getCategoryId() + DOESNT_EXIST));
            existingProduct.setCategoryId(updatedProduct.getCategoryId());
            existingProduct.setCategory(category);
        }
    }

    private void updateStatusIfProvided(Product updatedProduct, Product existingProduct) {
        if (updatedProduct.getStatusId() != null) {
            Status status = statusRepository.findById(updatedProduct.getStatusId())
                    .orElseThrow(() -> new IllegalArgumentException("El estado con ID " + updatedProduct.getStatusId() + DOESNT_EXIST));
            existingProduct.setStatusId(updatedProduct.getStatusId());
            existingProduct.setStatus(status);
        }
    }

    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
}