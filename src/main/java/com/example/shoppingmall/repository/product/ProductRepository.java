package com.example.shoppingmall.repository.product;

import com.example.shoppingmall.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
