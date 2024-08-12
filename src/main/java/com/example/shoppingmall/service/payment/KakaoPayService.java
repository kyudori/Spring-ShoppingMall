package com.example.shoppingmall.service.payment;

import com.example.shoppingmall.domain.order.Order;
import com.example.shoppingmall.domain.payment.Payment;
import com.example.shoppingmall.domain.payment.PaymentItem;
import com.example.shoppingmall.domain.user.User;
import com.example.shoppingmall.repository.order.OrderRepository;
import com.example.shoppingmall.repository.payment.PaymentItemRepository;
import com.example.shoppingmall.repository.payment.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class KakaoPayService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentItemRepository paymentItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Value("${kakaopay.cid}")
    private String cid;

    @Value("${kakaopay.secret-key}")
    private String secretKey;

    @Value("${kakaopay.api-url}")
    private String apiUrl;

    @Value("${kakaopay.approval-url}")
    private String approvalUrl;

    @Value("${kakaopay.cancel-url}")
    private String cancelUrl;

    @Value("${kakaopay.fail-url}")
    private String failUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, String> preparePayment(Order order, String partnerOrderId, String partnerUserId) {
        String url = apiUrl + "/v1/payment/ready";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "SECRET_KEY " + secretKey);
        headers.add("Content-Type", "application/json");

        Map<String, Object> params = new HashMap<>();
        params.put("cid", cid);
        params.put("partner_order_id", order.getId().toString());
        params.put("partner_user_id", order.getUser().getUserid());
        params.put("item_name", "Order-" + order.getId());
        params.put("quantity", order.getItems().size());
        params.put("total_amount", (int) order.getTotalAmount());
        params.put("tax_free_amount", 0);
        params.put("approval_url", approvalUrl);
        params.put("cancel_url", cancelUrl);
        params.put("fail_url", failUrl);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCodeValue() == 200) {
            Map<String, String> responseData = (Map<String, String>) response.getBody();
            String tid = responseData.get("tid");

            // TID를 주문에 저장 (추후 결제 승인 시 사용)
            order.setTid(tid);
            orderRepository.save(order);

            return responseData;
        } else {
            throw new RuntimeException("KakaoPay payment preparation failed");
        }
    }

    public Map<String, Object> approvePayment(String pgToken, String orderId) {
        String url = apiUrl + "/v1/payment/approve";

        Order order = orderRepository.findById(Long.parseLong(orderId))
                .orElseThrow(() -> new RuntimeException("Order not found"));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "SECRET_KEY " + secretKey);
        headers.add("Content-Type", "application/json");

        Map<String, Object> params = new HashMap<>();
        params.put("cid", cid);
        params.put("tid", order.getTid());
        params.put("partner_order_id", orderId);
        params.put("partner_user_id", order.getUser().getUserid());
        params.put("pg_token", pgToken);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCodeValue() == 200) {
            Map<String, Object> responseData = response.getBody();

            // 결제 완료 처리
            order.setStatus("COMPLETED");
            orderRepository.save(order);

            return responseData;
        } else {
            throw new RuntimeException("KakaoPay payment approval failed");
        }
    }

    private void savePaymentInfo(User user, Order order, Map<String, Object> paymentData) {
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setOrderId(order.getId().toString());
        payment.setTid((String) paymentData.get("tid"));
        payment.setPaymentMethodType((String) paymentData.get("payment_method_type"));
        payment.setTotalAmount((int) paymentData.get("amount.total"));
        payment.setApprovedAt(LocalDateTime.parse((String) paymentData.get("approved_at")));

        // Payment 엔티티 저장
        Payment savedPayment = paymentRepository.save(payment);

        // OrderItem을 PaymentItem으로 변환하여 저장
        List<PaymentItem> paymentItems = order.getItems().stream().map(orderItem -> {
            PaymentItem paymentItem = new PaymentItem();
            paymentItem.setPayment(savedPayment);
            paymentItem.setProduct(orderItem.getProduct());
            paymentItem.setQuantity(orderItem.getQuantity());
            paymentItem.setPrice(orderItem.getPrice()); // 상품별 가격

            return paymentItem;
        }).collect(Collectors.toList());

        paymentItemRepository.saveAll(paymentItems);
    }
}
