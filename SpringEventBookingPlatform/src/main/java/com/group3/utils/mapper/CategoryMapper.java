package com.group3.utils.mapper;

import com.group3.pojo.Category;
import com.group3.pojo.response.ResCategoryDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapper {

    /**
     * Convert Category entity to ResCategoryDTO
     */
    public static ResCategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }
        
        return new ResCategoryDTO(
            category.getId(),
            category.getName(),
            category.getActive(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }

    /**
     * Convert List of Categories to List of ResCategoryDTOs
     */
    public static List<ResCategoryDTO> toDTOList(List<Category> categories) {
        if (categories == null) {
            return new ArrayList<>();
        }
        return categories.stream()
                .map(CategoryMapper::toDTO)
                .collect(Collectors.toList());
    }
}
