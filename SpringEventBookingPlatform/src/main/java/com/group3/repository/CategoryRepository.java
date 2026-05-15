package com.group3.repository;

import com.group3.pojo.Category;
import java.util.List;

public interface CategoryRepository {
     List<Category> getCategories();
     Category getCateById(Integer id);
     boolean deleteCategory(Integer id);
     void addOrUpdateCategory(Category cate);
}
