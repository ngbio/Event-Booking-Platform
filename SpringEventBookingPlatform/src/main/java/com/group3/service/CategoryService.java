package com.group3.service;

import com.group3.dto.request.CategoryRequest;
import com.group3.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getCategories();
    CategoryResponse getCateById(Integer id);
    boolean deleteCategory(Integer id);
    CategoryResponse addOrUpdateCategory(CategoryRequest cate);
}
