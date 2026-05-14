package com.group3.service.impl;

import com.group3.dto.response.CategoryResponse;
import com.group3.pojo.Category;
import com.group3.repository.CategoryRepository;
import com.group3.service.CategoryService;
import com.group3.utils.DtoMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    private CategoryRepository categoryRepository;
    public List<CategoryResponse> getCategories() {
        List<Category> categories =  categoryRepository.getCategories();
        return DtoMapper.toCategoryResponseList(categories);
    }
}
