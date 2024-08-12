package com.example.shoppingmall.controller.payment;

import com.example.shoppingmall.domain.order.Order;
import com.example.shoppingmall.repository.order.OrderRepository;
import com.example.shoppingmall.service.order.OrderService;
import com.example.shoppingmall.service.payment.KakaoPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private KakaoPayService kakaoPayService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/kakao/ready")
    public ResponseEntity<Map<String, String>> prepareKakaoPayment(@AuthenticationPrincipal UserDetails userDetails,
                                                                   @RequestBody Map<String, String> request) {
        String orderIdStr = request.get("orderId");
        Order order = orderService.getOrderById(Long.parseLong(orderIdStr));

        String partnerOrderId = userDetails.getUsername() + "-" + order.getItems().get(0).getProduct().getName();
        String partnerUserId = userDetails.getUsername();

        order.setPartnerOrderId(partnerOrderId); // 가상의 partnerOrderId 필드를 설정
        orderRepository.save(order); // DB에 업데이트

        Map<String, String> response = kakaoPayService.preparePayment(order, partnerOrderId, partnerUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/kakao/approve")
    public ResponseEntity<Map<String, Object>> approveKakaoPayment(@RequestParam("pg_token") String pgToken,
                                                                   @RequestParam("orderId") String orderId) {
        try {
            Long orderIdLong = Long.parseLong(orderId);
            Map<String, Object> response = kakaoPayService.approvePayment(pgToken, orderId);
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Order ID must be a valid number"));
        }
    }
}
