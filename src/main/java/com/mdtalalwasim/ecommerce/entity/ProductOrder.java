package com.mdtalalwasim.ecommerce.entity;

import java.util.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn; // New import
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table; // New import
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "product_orders") // Changed table name
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderId;
    private Date orderDate;
    
    @ManyToOne
    private Product product;
    
    private Double price;
    
    private Integer quantity;
    
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id") // Added JoinColumn
    private User user;
    
    private String status;
    
    private String paymentType;
    
    @OneToOne(cascade = CascadeType.ALL)
    private OrderAddress orderAddress;
}