package com.group11.service;

import com.group11.dto.request.LineItemRequest;
import com.group11.dto.response.CheckoutResponse;
import com.group11.dto.response.OrderResponse;
import com.group11.entity.OrderEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IOrderService {
    public OrderResponse toOrderResponse(OrderEntity order);
    public Page<OrderResponse> getAllOrders(int page, int size);
    public OrderResponse getOrderById(Long id);
    public Page<OrderResponse> getAllOrdersByUserId(Long id, int page, int size);
    public Page<OrderResponse> searchOrders(String keyword, int page, int size);
    public OrderEntity createOrder(String token, CheckoutResponse response);
    public CheckoutResponse checkOutOrder(String token, List<LineItemRequest> cartItems);
    public void setDelivering(Long id);
    public void setDeliveried(Long id);
}
