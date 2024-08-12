package com.example.shoppingmall.domain.order;

import com.example.shoppingmall.domain.user.User;
import com.example.shoppingmall.domain.product.Product;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime orderDate;

    private String status; // 예: "PENDING", "COMPLETED", "CANCELLED"

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    private double totalAmount;

    @Getter
    @Setter
    private String partnerOrderId;
}
