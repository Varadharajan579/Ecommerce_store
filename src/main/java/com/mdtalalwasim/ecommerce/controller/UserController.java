package com.mdtalalwasim.ecommerce.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mdtalalwasim.ecommerce.entity.Cart;
import com.mdtalalwasim.ecommerce.entity.Category;
import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.entity.User;
import com.mdtalalwasim.ecommerce.service.CartService;
import com.mdtalalwasim.ecommerce.service.CategoryService;
import com.mdtalalwasim.ecommerce.service.ProductService;
import com.mdtalalwasim.ecommerce.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final double SHIPPING_COST = 250.0;
    private static final double TAX_AMOUNT = 100.0;

    @Autowired
    CategoryService categoryService;

    @Autowired
    UserService userService;

    @Autowired
    CartService cartService;

    @Autowired
    ProductService productService;

    @ModelAttribute
    public void getUserDetails(Principal principal, Model model) {
        if (principal != null) {
            String currentLoggedInUserEmail = principal.getName();
            User currentUserDetails = userService.getUserByEmail(currentLoggedInUserEmail);
            model.addAttribute("currentLoggedInUserDetails", currentUserDetails);

            Long countCartForUser = cartService.getCounterCart(currentUserDetails.getId());
            model.addAttribute("countCartForUser", countCartForUser);
        }

        List<Category> allActiveCategory = categoryService.findAllActiveCategory();
        model.addAttribute("allActiveCategory", allActiveCategory);
    }

    // ✅ User home page (show active products)
    @GetMapping("/")
    public String home(Model model) {
        List<Product> allActiveProducts = productService.findAllActiveProducts(null); 
        model.addAttribute("allActiveProducts", allActiveProducts);
        return "user/user-home"; // create this page to list products
    }

    // ✅ View single product details
    @GetMapping("/product")
    public String productDetails(@RequestParam("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null || !product.getIsActive()) {
            return "redirect:/user/"; // redirect if product not found or inactive
        }
        model.addAttribute("product", product);
        return "user/product-detail"; // create this page
    }

    // ✅ Add to cart
    @GetMapping("/add-to-cart")
    String addToCart(@RequestParam Long productId, @RequestParam Long userId, HttpSession session) {
        if (productId == null || userId == null) {
            session.setAttribute("errorMsg", "Invalid product or user information");
            return "redirect:/user/";
        }

        Cart saveCart = cartService.saveCart(productId, userId);

        if (ObjectUtils.isEmpty(saveCart)) {
            session.setAttribute("errorMsg", "Failed to add product to cart");
        } else {
            session.setAttribute("successMsg", "Product added to cart successfully");
        }
        return "redirect:/user/product?id=" + productId;
    }

    // ✅ Cart Page
    @GetMapping("/cart")
    String loadCartPage(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = getLoggedUserDetails(principal);
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        model.addAttribute("carts", carts);

        if (carts.size() > 0) {
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            model.addAttribute("totalOrderPrice", totalOrderPrice);
        }

        return "user/cart";
    }

    @GetMapping("/cart-quantity-update")
    public String updateCartQuantity(@RequestParam("symbol") String symbol, @RequestParam("cartId") Long cartId) {
        if (symbol == null || cartId == null) {
            return "redirect:/user/cart";
        }

        cartService.updateCartQuantity(symbol, cartId);
        return "redirect:/user/cart";
    }

    private User getLoggedUserDetails(Principal principal) {
        if (principal == null) {
            return null;
        }
        String email = principal.getName();
        return userService.getUserByEmail(email);
    }

    // ✅ Orders Page
    @GetMapping("/orders")
    public String orderPage(Principal principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = getLoggedUserDetails(principal);
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        model.addAttribute("carts", carts);

        if (carts.size() > 0) {
            Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            Double totalOrderPrice = orderPrice + SHIPPING_COST + TAX_AMOUNT;
            model.addAttribute("orderPrice", orderPrice);
            model.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "user/order";
    }
}
