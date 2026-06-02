package com.group3.repository;

import com.group3.pojo.Category;
import java.util.List;

public interface CategoryRepository {
     List<Category> getCategories();
     Category getCateById(Integer id);
     void deleteCategory(Category cate);
     void addOrUpdateCategory(Category cate);
}
