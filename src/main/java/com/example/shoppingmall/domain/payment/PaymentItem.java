package com.example.shoppingmall.domain.payment;

import com.example.shoppingmall.domain.product.Product;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PaymentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
    private double price; // 각 물품의 구매 금액
}
