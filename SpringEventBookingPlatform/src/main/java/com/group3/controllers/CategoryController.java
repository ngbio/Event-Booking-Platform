package com.group3.controllers;

import com.group3.dto.request.CategoryRequest;
import com.group3.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {







    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listCategory(Model model) {
        model.addAttribute("categories", this.categoryService.getCategories());
        model.addAttribute("activePage", "categories");
        return "categories";
    }

    @PostMapping("/delete")
    public String deleteCategory(@RequestParam("categoryId") int id, RedirectAttributes redirectAttributes) {
        try {
            boolean isDeleted = this.categoryService.deleteCategory(id);
            if (isDeleted) {
                redirectAttributes.addFlashAttribute("success", "Đã xóa vĩnh viễn danh mục thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không thể xóa! Danh mục này đang chứa sự kiện.");
            }
        } catch (Exception e) { 
            redirectAttributes.addFlashAttribute("error", "Xóa thất bại! Không tìm thấy danh mục.");
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/update/{id}")
    public String editView(Model model, @PathVariable("id") int id) {
        model.addAttribute("category", this.categoryService.getCateById(id));
        return "category-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("category") CategoryRequest c) {
        this.categoryService.addOrUpdateCategory(c);
        return "redirect:/admin/categories";
    }

    @GetMapping("/add")
    public String addCate(Model model) {
        model.addAttribute("category", new CategoryRequest());
        return "category-form";
    }
}
