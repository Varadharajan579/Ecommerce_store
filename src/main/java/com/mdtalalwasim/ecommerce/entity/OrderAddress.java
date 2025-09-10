package com.mdtalalwasim.ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table; // New import
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "order_addresses") // Changed table name
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class OrderAddress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private String mobile;
    
    private String address;
    
    private String city;
    
    private String state;
    
    private String pinCode;
}