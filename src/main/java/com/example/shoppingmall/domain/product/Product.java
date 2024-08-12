package com.example.shoppingmall.domain.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private double price;

    @NotNull
    private int stock;

    private String imageUrl;

//    @ManyToOne
//    private Category category; // 카테고리 엔티티와 연관 (추후 구현 가능)
}
