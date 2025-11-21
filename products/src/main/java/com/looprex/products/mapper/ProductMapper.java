package com.looprex.products.mapper;

import com.looprex.products.dto.ProductResponse;
import com.looprex.products.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    private final CategoryMapper categoryMapper;
    private final StatusMapper statusMapper;

    public ProductMapper(CategoryMapper categoryMapper, StatusMapper statusMapper) {
        this.categoryMapper = categoryMapper;
        this.statusMapper = statusMapper;
    }

    /**
     * Convierte una entidad Product a ProductResponse DTO
     */
    public ProductResponse toProductResponse(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponse response = new ProductResponse();
        response.setProductId(product.getProductId());
        response.setStock(product.getStock());
        response.setProductPhoto(product.getProductPhoto());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStatusId(product.getStatusId());
        response.setCategoryId(product.getCategoryId());

        // Mapear categoría anidada si existe
        if (product.getCategory() != null) {
            response.setCategory(categoryMapper.toCategoryResponse(product.getCategory()));
        }

        // Mapear estado anidado si existe
        if (product.getStatus() != null) {
            response.setStatus(statusMapper.toStatusResponse(product.getStatus()));
        }

        return response;
    }
}