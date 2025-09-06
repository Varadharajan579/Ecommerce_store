package com.mdtalalwasim.ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.mdtalalwasim.ecommerce.entity.Product;

public interface ProductService {

    Product saveProduct(Product product, MultipartFile file);

    List<Product> getAllProducts();

    Boolean deleteProduct(long id);

    Optional<Product> findById(long id);

    Product getProductById(long id);
    
    Product updateProductById(Product product, MultipartFile file);
    
    List<Product> findAllActiveProducts(String category);
}
