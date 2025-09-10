package com.mdtalalwasim.ecommerce.service.impl;

import com.mdtalalwasim.ecommerce.entity.Product;
import com.mdtalalwasim.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    private ProductRepository productRepository;
    private ProductServiceImpl productService;

    @BeforeEach
void setUp() {
    productRepository = mock(ProductRepository.class);
    productService = new ProductServiceImpl(productRepository); // inject mock
}

    @Test
    void testSaveProduct() throws Exception {
        Product product = new Product();
        product.setProductTitle("Laptop");

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true); // simulate no file

        when(productRepository.save(product)).thenReturn(product);

        Product saved = productService.saveProduct(product, file);

        assertEquals("Laptop", saved.getProductTitle());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testGetAllProducts() {
        Product p1 = new Product();
        Product p2 = new Product();
        when(productRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Product> products = productService.getAllProducts();

        assertEquals(2, products.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testDeleteProduct_ProductExists() {
        Product product = new Product();
        product.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById(1L);

        Boolean deleted = productService.deleteProduct(1L);

        assertTrue(deleted);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProduct_ProductNotExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Boolean deleted = productService.deleteProduct(1L);

        assertFalse(deleted);
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    void testFindById() {
        Product product = new Product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.findById(1L);

        assertTrue(result.isPresent());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_ProductExists() {
        Product product = new Product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertNotNull(result);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_ProductNotExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Product result = productService.getProductById(1L);

        assertNull(result);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateProductById_ProductExists() throws Exception {
        Product dbProduct = new Product();
        dbProduct.setId(1L);
        dbProduct.setProductTitle("Old Laptop");

        Product update = new Product();
        update.setId(1L);
        update.setProductTitle("New Laptop");

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        when(productRepository.findById(1L)).thenReturn(Optional.of(dbProduct));
        when(productRepository.save(dbProduct)).thenReturn(dbProduct);

        Product updated = productService.updateProductById(update, file);

        assertEquals("New Laptop", updated.getProductTitle());
        verify(productRepository, times(1)).save(dbProduct);
    }

    @Test
    void testUpdateProductById_ProductNotExists() {
        Product update = new Product();
        update.setId(1L);

        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Product updated = productService.updateProductById(update, file);

        assertNull(updated);
        verify(productRepository, never()).save(any());
    }

    @Test
    void testFindAllActiveProducts_WithCategory() {
        Product p = new Product();
        p.setProductCategory("Electronics");

        when(productRepository.findByProductCategoryAndIsActiveTrue("Electronics"))
                .thenReturn(Arrays.asList(p));

        List<Product> products = productService.findAllActiveProducts("Electronics");

        assertEquals(1, products.size());
        verify(productRepository, times(1)).findByProductCategoryAndIsActiveTrue("Electronics");
    }

    @Test
    void testFindAllActiveProducts_NoCategory() {
        Product p = new Product();

        when(productRepository.findByIsActiveTrue())
                .thenReturn(Arrays.asList(p));

        List<Product> products = productService.findAllActiveProducts(null);

        assertEquals(1, products.size());
        verify(productRepository, times(1)).findByIsActiveTrue();
    }
}
