package com.example.shoppingmall.controller.cart;

import com.example.shoppingmall.domain.cart.Cart;
import com.example.shoppingmall.domain.user.User;
import com.example.shoppingmall.service.cart.CartService;
import com.example.shoppingmall.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Cart> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUserid(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(cartService.getCart(user));
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addItemToCart(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestBody Map<String, Object> payload) {
        User user = userService.findByUserid(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long productId = Long.valueOf(payload.get("productId").toString());
        int quantity = Integer.parseInt(payload.get("quantity").toString());

        return ResponseEntity.ok(cartService.addItemToCart(user, productId, quantity));
    }

    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<Cart> updateItemInCart(@AuthenticationPrincipal UserDetails userDetails,
                                                 @PathVariable("cartItemId") Long cartItemId,
                                                 @RequestBody Map<String, Object> payload) {

        User user = userService.findByUserid(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        int quantity = Integer.parseInt(payload.get("quantity").toString());

        return ResponseEntity.ok(cartService.updateItemInCart(user, cartItemId, quantity));
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<Cart> removeItemFromCart(@AuthenticationPrincipal UserDetails userDetails,
                                                   @PathVariable("cartItemId") Long cartItemId) {

        User user = userService.findByUserid(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(cartService.removeItemFromCart(user, cartItemId));
    }
}
