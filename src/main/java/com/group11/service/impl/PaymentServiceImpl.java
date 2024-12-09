package com.group11.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group11.config.VNPAYConfig;
import com.group11.dto.EmailDetail;
import com.group11.dto.PaymentDTO;
import com.group11.entity.*;
import com.group11.repository.OrderRepository;
import com.group11.repository.PaymentRepository;
import com.group11.service.IInventoryService;
import com.group11.service.IPaymentService;
import com.group11.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final VNPAYConfig vnPayConfig;
    @Autowired
    private final OrderRepository orderRepository;
    @Autowired
    private final PaymentRepository paymentRepository;
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private IInventoryService inventoryService;

    @Override
    public PaymentDTO createVnPayPayment(HttpServletRequest request) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");
        String orderId = request.getParameter("orderId");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(orderId);
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentDTO.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }
    @Override
    public void handlePayBank(String transactionNo, String bankCode, String transactionStatus, LocalDateTime localDateTime, int amount, Long orderId){
        Optional<OrderEntity> orderOtn = orderRepository.findById(orderId);
        OrderEntity order = orderOtn.get();
        order.setPaymentStatus(PaymentStatus.PAID);

        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setTransactionID(transactionNo);
        paymentEntity.setPaymentMethod(bankCode);
        paymentEntity.setPaymentStatus(PaymentStatus.COMPLETED);
        paymentEntity.setPaymentDate(localDateTime);
        paymentEntity.setTotal(amount);
        paymentEntity.setOrder(order);

        paymentRepository.save(paymentEntity);
        //order.setPayment(paymentEntity);
        orderRepository.save(order);

        List<LineItemEntity> lineItem = order.getListLineItems();
        for (LineItemEntity lineItemEntity : lineItem) {
            InventoryEntity inventoryEntity = inventoryService.findById(lineItemEntity.getProduct().getProductID()).get();
            inventoryEntity.setQuantity(inventoryEntity.getQuantity() - lineItemEntity.getQuantity());
            inventoryService.save(inventoryEntity);
        }

        EmailDetail emailDetail = new EmailDetail();

        String body = "Chào " + order.getUser().getName() + ",\n\n" +
                "Chúng tôi xác nhận rằng bạn đã thanh toán thành công cho đơn hàng (Mã đơn hàng: " + orderId + ").\n" +
                "Số tiền thanh toán: " + amount + " VND.\n" +
                "Ngày thanh toán: " + localDateTime + ".\n\n" +
                "Cảm ơn bạn đã tin tưởng và mua sắm tại cửa hàng của chúng tôi.\n\n" +
                "Trân trọng,\nYour Company Name";
        emailDetail.setMsgBody(body);
        emailDetail.setRecipient(order.getUser().getEmail());
        emailDetail.setSubject("Thông báo thánh toán đơn hàng");
        emailService.sendInvoice(emailDetail);
    }

}
