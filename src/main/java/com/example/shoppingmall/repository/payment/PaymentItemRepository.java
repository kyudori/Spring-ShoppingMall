package com.example.shoppingmall.repository.payment;

import com.example.shoppingmall.domain.payment.PaymentItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentItemRepository extends JpaRepository<PaymentItem, Long> {
}
