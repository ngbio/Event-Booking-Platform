package com.group3.controllers;

import com.group3.dto.response.ApiResponse;
import com.group3.dto.response.CategoryResponse;
import com.group3.service.CategoryService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ApiCategoryController {
    @Autowired
    private CategoryService cateService;
    
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> list(){
        //return new ResponseEntity<>(this.cateService.getCategories(),HttpStatus.OK);
        List<CategoryResponse> categories = this.cateService.getCategories();
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy thông tin danh lục thành công",categories));
    } 
}
