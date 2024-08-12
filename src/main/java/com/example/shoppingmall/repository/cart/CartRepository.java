package com.example.shoppingmall.repository.cart;

import com.example.shoppingmall.domain.cart.Cart;
import com.example.shoppingmall.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
