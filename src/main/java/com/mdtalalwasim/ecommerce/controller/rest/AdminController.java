package com.mdtalalwasim.ecommerce.controller.rest;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mdtalalwasim.ecommerce.entity.Category;
import com.mdtalalwasim.ecommerce.service.CategoryService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private CategoryService categoryService;
    
    @PostMapping("/save-category")
    public String saveCategory(@ModelAttribute Category category) {
        categoryService.saveCategory(category);
        return "redirect:/category";  // goes back to category page
    }
}
