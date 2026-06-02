package com.group3.service.impl;

import com.group3.dto.request.CategoryRequest;
import com.group3.dto.response.CategoryResponse;
import com.group3.exceptions.BusinessException;
import com.group3.pojo.Category;
import com.group3.repository.CategoryRepository;
import com.group3.service.CategoryService;
import com.group3.utils.DTOMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryServiceImpl implements CategoryService{
    @Autowired
    private CategoryRepository categoryRepo;
    
    @Override
    public List<CategoryResponse> getCategories() {
        List<Category> categories =  categoryRepo.getCategories();
        return DTOMapper.toCategoryResponseList(categories);
    }
    
    @Override
    public CategoryResponse getCateById(Integer id){
        return DTOMapper.toCategoryResponse(this.categoryRepo.getCateById(id));
    }
    
    @Override
    public boolean deleteCategory(Integer id){
        Category cate = this.categoryRepo.getCateById(id);
        if (cate == null) return false;
        if (cate.getEventCollection()!=null&&!cate.getEventCollection().isEmpty())
            throw new BusinessException("Đang tồn tại sự kiện trong danh mục");
        this.categoryRepo.deleteCategory(cate);
        return true;
    }
    
    @Override
    @Transactional
    public CategoryResponse addOrUpdateCategory(CategoryRequest request){
        Category cate = DTOMapper.toCategoryEntity(request);
        this.categoryRepo.addOrUpdateCategory(cate);
        return DTOMapper.toCategoryResponse(cate);
    }
}
