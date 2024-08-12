package com.example.shoppingmall.controller.order;

import com.example.shoppingmall.domain.order.Order;
import com.example.shoppingmall.domain.order.OrderItem;
import com.example.shoppingmall.domain.product.Product;
import com.example.shoppingmall.domain.user.User;
import com.example.shoppingmall.service.order.OrderService;
import com.example.shoppingmall.service.product.ProductService;
import com.example.shoppingmall.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@AuthenticationPrincipal UserDetails userDetails,
                                             @RequestBody Map<String, Object> orderData) {
        User user = userService.findByUserid(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Map<String, Object>> itemsData = (List<Map<String, Object>>) orderData.get("items");
        List<OrderItem> items = itemsData.stream().map(itemData -> {
            OrderItem item = new OrderItem();
            Long productId = Long.valueOf(itemData.get("productId").toString());

            // Product를 가져와 설정
            Product product = productService.getProductById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            item.setProduct(product);
            item.setQuantity(Integer.parseInt(itemData.get("quantity").toString()));
            item.setPrice(Double.parseDouble(itemData.get("price").toString()));
            return item;
        }).collect(Collectors.toList());

        double totalAmount = Double.parseDouble(orderData.get("totalAmount").toString());

        Order order = orderService.createOrder(user, items, totalAmount);

        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUserid(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderService.getOrdersForUser(user.getId());
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/update-status/{orderId}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long orderId,
                                                  @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok().build();
    }
}
