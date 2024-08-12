package com.example.shoppingmall.service.order;

import com.example.shoppingmall.domain.order.Order;
import com.example.shoppingmall.domain.order.OrderItem;
import com.example.shoppingmall.domain.product.Product;
import com.example.shoppingmall.domain.user.User;
import com.example.shoppingmall.repository.order.OrderRepository;
import com.example.shoppingmall.repository.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public Order createOrder(User user, List<OrderItem> items, double totalAmount) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setItems(items);
        order.setTotalAmount(totalAmount);
        order.setTid(null);

        for (OrderItem item : items) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        return orderRepository.save(order);
    }

    public List<Order> getOrdersForUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    // 주문 조회 시 TID가 필요하다면 관련 로직 추가
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
