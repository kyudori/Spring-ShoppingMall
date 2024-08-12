package com.example.shoppingmall.domain.payment;

import com.example.shoppingmall.domain.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String orderId;
    private String tid;
    private String paymentMethodType;
    private int totalAmount;
    private LocalDateTime approvedAt;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentItem> items;
}
