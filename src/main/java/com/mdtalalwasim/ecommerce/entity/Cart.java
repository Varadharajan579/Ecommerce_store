package com.mdtalalwasim.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn; // New import
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table; // New import
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "carts") // Changed table name
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Cart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id") // Added JoinColumn
    private User user;
    
    @ManyToOne
    private Product product;
    
    private Integer quantity;
    
    @Transient
    private Double totalPrice;
    
    @Transient
    private Double totalOrderPrice;
}