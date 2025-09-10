package com.mdtalalwasim.ecommerce.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.repository.ProductRepository;
import com.mdtalalwasim.ecommerce.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
     public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
     }

    // ✅ Save new product with image
    @Override
    public Product saveProduct(Product product, MultipartFile file) {
        try {
            String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
            product.setProductImage(imageName);

            product.calculateDiscountPrice(); // calculate discount before saving
            Product savedProduct = productRepository.save(product);

            // Save file into static/img/product_image
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img/product_image").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            return savedProduct;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ✅ Get all products (admin)
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // ✅ Delete product
    @Override
    public Boolean deleteProduct(long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            productRepository.deleteById(product.get().getId());
            return true;
        }
        return false;
    }

    // ✅ Find product by ID (Optional)
    @Override
    public Optional<Product> findById(long id) {
        return productRepository.findById(id);
    }

    // ✅ Get product by ID (return null if not found)
    @Override
    public Product getProductById(long id) {
        return productRepository.findById(id).orElse(null);
    }

    // ✅ Update product
    @Override
    public Product updateProductById(Product product, MultipartFile file) {
        Product dbProduct = getProductById(product.getId());
        if (dbProduct == null) {
            return null; // Product not found
        }

        // Handle image
        String imageName = file.isEmpty() ? dbProduct.getProductImage() : file.getOriginalFilename();
        dbProduct.setProductImage(imageName);

        // Update fields
        dbProduct.setProductTitle(product.getProductTitle());
        dbProduct.setProductDescription(product.getProductDescription());
        dbProduct.setProductCategory(product.getProductCategory());
        dbProduct.setProductPrice(product.getProductPrice());
        dbProduct.setProductStock(product.getProductStock());
        dbProduct.setIsActive(product.getIsActive());
        dbProduct.setDiscount(product.getDiscount());

        dbProduct.calculateDiscountPrice();

        Product updatedProduct = productRepository.save(dbProduct);

        // Save updated image if provided
        if (!ObjectUtils.isEmpty(updatedProduct) && !file.isEmpty()) {
            try {
                File saveFile = new ClassPathResource("static/img/product_image").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return updatedProduct;
    }

    // ✅ Find all active products (for users)
    @Override
    public List<Product> findAllActiveProducts(String category) {
        if (ObjectUtils.isEmpty(category)) {
            return productRepository.findByIsActiveTrue();
        }
        return productRepository.findByProductCategoryAndIsActiveTrue(category);
    }
}
