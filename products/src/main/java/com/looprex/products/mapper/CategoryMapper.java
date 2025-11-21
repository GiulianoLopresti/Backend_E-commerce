package com.looprex.products.mapper;

import com.looprex.products.dto.CategoryResponse;
import com.looprex.products.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    /**
     * Convierte una entidad Category a CategoryResponse DTO
     */
    public CategoryResponse toCategoryResponse(Category category) {
        if (category == null) {
            return null;
        }

        return new CategoryResponse(
            category.getCategoryId(),
            category.getName()
        );
    }
}