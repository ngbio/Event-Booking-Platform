package com.group3.repository;

import com.group3.pojo.Category;
import java.util.List;

public interface CategoryRepository {
     List<Category> getCates();
     Category getCateById(Integer id);
}
