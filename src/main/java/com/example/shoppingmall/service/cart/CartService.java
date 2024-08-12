package com.example.shoppingmall.service.cart;

import com.example.shoppingmall.domain.cart.Cart;
import com.example.shoppingmall.domain.cart.CartItem;
import com.example.shoppingmall.domain.product.Product;
import com.example.shoppingmall.domain.user.User;
import com.example.shoppingmall.repository.cart.CartItemRepository;
import com.example.shoppingmall.repository.cart.CartRepository;
import com.example.shoppingmall.repository.product.ProductRepository;
import com.example.shoppingmall.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public Cart getCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> createCart(user));
    }

    public Cart createCart(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    public Cart addItemToCart(User user, Long productId, int quantity) {
        Cart cart = getCart(user);
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);

        cart.getItems().add(cartItem);
        cartItemRepository.save(cartItem);

        return cartRepository.save(cart);
    }

    public Cart updateItemInCart(User user, Long cartItemId, int quantity) {
        Cart cart = getCart(user);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cartItem.getCart().equals(cart)) {
            throw new RuntimeException("Unauthorized access");
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        return cart;
    }

    public Cart removeItemFromCart(User user, Long cartItemId) {
        Cart cart = getCart(user);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!cartItem.getCart().equals(cart)) {
            throw new RuntimeException("Unauthorized access");
        }

        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);

        return cart;
    }
}
