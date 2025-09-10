package com.mdtalalwasim.ecommerce.controller;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.mdtalalwasim.ecommerce.entity.Category;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.service.CartService;
import com.mdtalalwasim.ecommerce.service.CategoryService;
import com.mdtalalwasim.ecommerce.service.ProductService;
import com.mdtalalwasim.ecommerce.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    @Autowired
    CartService cartService;

    // ✅ Common user details
    @ModelAttribute
    public void getUserDetails(Principal principal, Model model) {
        if (principal != null) {
            String currenLoggedInUserEmail = principal.getName();
            User currentUserDetails = userService.getUserByEmail(currenLoggedInUserEmail);

            if (currentUserDetails != null) {
                model.addAttribute("currentLoggedInUserDetails", currentUserDetails);
                Long countCartForUser = cartService.getCounterCart(currentUserDetails.getId());
                model.addAttribute("countCartForUser", countCartForUser);
            }
        }
    }

    // ✅ Admin dashboard
    @GetMapping("/")
    public String adminIndex() {
        return "admin/admin-dashboard";
    }

    // ================= CATEGORY MODULE =================

    @GetMapping("/add-category")
    public String addCategory(Model model) {
        return "admin/category/category-add-form";
    }

    @PostMapping("/save-category")
    public String saveCategory(@ModelAttribute Category category,
                               @RequestParam("file") MultipartFile file,
                               HttpSession session) {
        try {
            String imageName = (file != null && !file.isEmpty()) ? file.getOriginalFilename() : "default.jpg";
            category.setCategoryImage(imageName);

            if (categoryService.existCategory(category.getCategoryName())) {
                session.setAttribute("errorMsg", "Category Name already Exists");
            } else {
                Category savedCategory = categoryService.saveCategory(category);
                if (ObjectUtils.isEmpty(savedCategory)) {
                    session.setAttribute("errorMsg", "Not Saved! Internal Server Error!");
                } else {
                    session.setAttribute("successMsg", "Category Saved Successfully.");
                }
            }
        } catch (Exception e) {
            session.setAttribute("errorMsg", "Error while saving category!");
            e.printStackTrace();
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/category")
    public String category(Model model) {
        List<Category> allCategories = categoryService.getAllCategories();
        for (Category category : allCategories) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String format = formatter.format(category.getCreatedAt());
            model.addAttribute("formattedDateTimeCreatedAt", format);
        }
        model.addAttribute("allCategoryList", allCategories);
        return "/admin/category/category-home";
    }

    @GetMapping("/edit-category/{id}")
    public String editCategoryForm(@PathVariable("id") long id, Model model) {
        Optional<Category> categoryObj = categoryService.findById(id);
        categoryObj.ifPresent(category -> model.addAttribute("category", category));
        return "/admin/category/category-edit-form";
    }

    @PostMapping("/update-category")
    public String updateCategory(@ModelAttribute Category category,
                                 @RequestParam("file") MultipartFile file,
                                 HttpSession session) {
        Optional<Category> categoryById = categoryService.findById(category.getId());
        if (categoryById.isPresent()) {
            Category oldCategory = categoryById.get();
            oldCategory.setCategoryName(category.getCategoryName());
oldCategory.setActive(category.isActive());


            String imageName = file.isEmpty() ? oldCategory.getCategoryImage() : file.getOriginalFilename();
            oldCategory.setCategoryImage(imageName);

            Category updatedCategory = categoryService.saveCategory(oldCategory);
            if (!ObjectUtils.isEmpty(updatedCategory)) {
                session.setAttribute("successMsg", "Category Updated Successfully");
            } else {
                session.setAttribute("errorMsg", "Something went wrong on server!");
            }
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/delete-category/{id}")
    public String deleteCategory(@PathVariable("id") long id, HttpSession session) {
        Boolean deleteCategory = categoryService.deleteCategory(id);
        if (deleteCategory) {
            session.setAttribute("successMsg", "Category Deleted Successfully");
        } else {
            session.setAttribute("errorMsg", "Server Error");
        }
        return "redirect:/admin/category";
    }

    // ================= PRODUCT MODULE =================

    @GetMapping("/add-product")
    public String addProduct(Model model) {
        model.addAttribute("allCategoryList", categoryService.getAllCategories());
        return "/admin/product/add-product";
    }

    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute Product product,
                              @RequestParam("file") MultipartFile file,
                              HttpSession session) {
        Product savedProduct = productService.saveProduct(product, file);
        if (!ObjectUtils.isEmpty(savedProduct)) {
            session.setAttribute("successMsg", "Product Saved Successfully.");
        } else {
            session.setAttribute("errorMsg", "Something went wrong while saving product.");
        }
        return "redirect:/admin/product-list";
    }

    @GetMapping("/product-list")
    public String productList(Model model) {
        model.addAttribute("productList", productService.getAllProducts());
        return "/admin/product/product-list";
    }

    @GetMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable("id") long id, HttpSession session) {
        Boolean deleteProduct = productService.deleteProduct(id);
        if (deleteProduct) {
            session.setAttribute("successMsg", "Product Deleted Successfully.");
        } else {
            session.setAttribute("errorMsg", "Something went wrong while deleting product.");
        }
        return "redirect:/admin/product-list";
    }

    @GetMapping("/edit-product/{id}")
    public String editProduct(@PathVariable long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("allCategoryList", categoryService.getAllCategories());
        return "/admin/product/edit-product";
    }

    @PostMapping("/update-product")
    public String updateProduct(@ModelAttribute Product product,
                                @RequestParam("file") MultipartFile file,
                                HttpSession session) {
        if (product.getDiscount() < 0 || product.getDiscount() > 100) {
            session.setAttribute("errorMsg", "INVALID DISCOUNT!");
        } else {
            Product updateProduct = productService.updateProductById(product, file);
            if (!ObjectUtils.isEmpty(updateProduct)) {
                session.setAttribute("successMsg", "Product Updated Successfully.");
            } else {
                session.setAttribute("errorMsg", "Something went wrong while updating product.");
            }
        }
        return "redirect:/admin/product-list";
    }

    // ================= USER MODULE =================

    @GetMapping("/get-all-users")
    public String getAllUser(Model model) {
        List<User> allUsers = userService.getAllUsersByRole("ROLE_USER");
        for (User user : allUsers) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String format = formatter.format(user.getCreatedAt());
            model.addAttribute("formattedDateTimeCreatedAt", format);
        }
        model.addAttribute("allUsers", allUsers);
        return "/admin/users/user-home";
    }

    @GetMapping("/edit-user-status")
    public String editUser(@RequestParam("status") Boolean status,
                           @RequestParam("id") Long id,
                           HttpSession session) {
        Boolean updateUserStatus = userService.updateUserStatus(status, id);
        if (updateUserStatus) {
            session.setAttribute("successMsg", "User Status Updated Successfully.");
        } else {
            session.setAttribute("errorMsg", "Something went wrong while updating user status.");
        }
        return "redirect:/admin/get-all-users";
    }
	@GetMapping("/product/{id}")
public String getProductDetail(@PathVariable Long id, Model model) {
    Product product = productService.getProductById(id); // fetch product by ID
    if (product == null) {
        return "error/404"; // redirect if product not found
    }
    model.addAttribute("product", product);
    return "user/product-detail"; // load the Thymeleaf file
}

}
