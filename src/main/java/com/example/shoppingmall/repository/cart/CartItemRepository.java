package com.example.shoppingmall.repository.cart;

import com.example.shoppingmall.domain.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
