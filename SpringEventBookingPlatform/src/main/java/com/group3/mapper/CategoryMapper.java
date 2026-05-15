package com.group3.mapper;

import com.group3.dto.request.CategoryRequest;
import com.group3.pojo.Category;
import com.group3.dto.response.CategoryResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapper {

    /**
     * Convert Category entity to ResCategoryDTO
     */
    public static CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }
        
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getActive(),
            category.getCreatedDate(),
            category.getUpdatedDate()
        );
    }

    /**
     * Convert List of Categories to List of ResCategoryDTOs
     */
    public static List<CategoryResponse> toResponseList(List<Category> categories) {
        if (categories == null) {
            return new ArrayList<>();
        }
        return categories.stream()
                .map(CategoryMapper::toResponse)
                .collect(Collectors.toList());
    }
    
    public static Category toEntity(CategoryRequest request){
        Category cate = new Category();
        if (request.getId()!=null)
            cate.setId(request.getId());
        cate.setName(request.getName());
        cate.setActive(request.isActive());
        Date now = new Date();
        cate.setCreatedDate(now);
        cate.setUpdatedDate(now);
        return cate;
    }
}
