package com.mdtalalwasim.ecommerce.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productTitle;
    private String productDescription;
    private String productCategory;
    private Double productPrice;
    private Integer productStock = 0;
    private String productImage;
    private Integer discount = 0;
    private Double discountPrice;
    private Boolean isActive = true;
    private Integer quantity = 0;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void calculateDiscountPrice() {
        if (discount != null && discount > 0 && productPrice != null) {
            this.discountPrice = productPrice - (productPrice * discount / 100.0);
        } else {
            this.discountPrice = productPrice;
        }
    }
}
