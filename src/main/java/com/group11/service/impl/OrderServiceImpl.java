package com.group11.service.impl;

import com.group11.dto.request.LineItemRequest;
import com.group11.dto.response.CheckoutResponse;
import com.group11.dto.response.LineItemResponse;
import com.group11.dto.response.OrderResponse;
import com.group11.entity.*;
import com.group11.repository.AddressRepository;
import com.group11.repository.OrderRepository;
import com.group11.repository.UserRepository;
import com.group11.service.*;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private IUserService userService;
    @Autowired
    private IJwtService jwtService;
    @Autowired
    IProductService productService;
    @Autowired
    IInventoryService inventoryService;

    @Override
    public Page<OrderResponse> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderEntity> orderPage = orderRepository.findAll(pageable);

        return orderPage.map(this::toOrderResponse);
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        return orderRepository.findById(id).map(this::toOrderResponse).orElse(null);
    }

    @Override
    public Page<OrderResponse> getAllOrdersByUserId(Long id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderEntity> orderPage = orderRepository.findAllOrderByUserUserID(id, pageable);
        return orderPage.map(this::toOrderResponse);
    }

    @Override
    public Page<OrderResponse> searchOrders(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        OrderShippingStatus status = null;

        // Kiểm tra nếu keyword khớp với enum
        try {
            status = OrderShippingStatus.valueOf(keyword.toUpperCase());
        } catch (IllegalArgumentException ex) {
            // Nếu keyword không khớp enum, bỏ qua
        }

        Page<OrderEntity> orderPage = orderRepository.searchByKeywordAndStatus(keyword,status, pageable);

        return orderPage.map(this::toOrderResponse);
    }

    @Override
    public OrderEntity createOrder(String token, CheckoutResponse response) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = jwtService.extractAllClaims(token);
        Long userID = claims.get("userid", Long.class);

        UserEntity user = userService.findById(userID);

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setReceiveDate(LocalDateTime.now().plusDays(7));
        order.setShippingStatus(OrderShippingStatus.NONDELIVERY);
        order.setPhoneNumber(response.getPhone());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setNote(response.getNote());

        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setCommune(response.getAddress().getCommune());
        addressEntity.setProvince(response.getAddress().getProvince());
        addressEntity.setDistrict(response.getAddress().getDistrict());
        addressEntity.setCountry(response.getAddress().getCountry());
        addressEntity.setOther(response.getAddress().getOther());

        addressRepository.save(addressEntity);

        order.setShippingAddress(addressEntity);


        List<LineItemEntity> list = new ArrayList<>();

        for (LineItemRequest item : response.getCartItems()){
            LineItemEntity lineItemEntity = new LineItemEntity();
            lineItemEntity.setProduct(productService.findProductById(item.getProductId()).get());
            lineItemEntity.setQuantity(item.getQuantity());
            lineItemEntity.setOrder(order);
            list.add(lineItemEntity);
        }
        order.setListLineItems(list);
        return orderRepository.save(order);
    }

    @Override
    public CheckoutResponse checkOutOrder(String token, List<LineItemRequest> cartItems) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = jwtService.extractAllClaims(token);
        String name = claims.get("name", String.class);
        String phone = claims.get("phone", String.class);
        String email = claims.getSubject();
        Map<String, Object> address = claims.get("address", Map.class);
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setAddressID(((Number) address.get("addressID")).longValue());
        addressEntity.setCountry((String) address.get("country"));
        addressEntity.setProvince((String) address.get("province"));
        addressEntity.setDistrict((String) address.get("district"));
        addressEntity.setCommune((String) address.get("commune"));

        AtomicInteger total = new AtomicInteger();

        // Kiểm tra số lượng tồn kho cho từng sản phẩm
        List<LineItemRequest> processedCartItems = cartItems.stream()
                .map(item -> {
                    // Lấy thông tin sản phẩm từ database
                    ProductEntity product = productService.findProductById(item.getProductId()).orElse(null);
                    if (product == null) {
                        throw new IllegalArgumentException("Sản phẩm không tồn tại: " + item.getProductId());
                    }

                    // Lấy số lượng tồn kho
                    int quantityInStock = inventoryService.findById(item.getProductId())
                            .map(inventory -> inventory.getQuantity())
                            .orElse(0);

                    // Kiểm tra nếu số lượng tồn kho không đủ
                    if (quantityInStock < item.getQuantity()) {
                        return null; // hoặc ném ngoại lệ mới nếu cần
                    }

                    // Tính giá trị mỗi sản phẩm
                    item.setProductName(product.getName());
                    item.setPrice(product.getPrice());
                    item.setTotal(item.getQuantity() * product.getPrice());
                    total.addAndGet(item.getQuantity() * product.getPrice());
                    return item;
                })
                .filter(Objects::nonNull) // Loại bỏ các mục null
                .collect(Collectors.toList());

        // Nếu có bất kỳ sản phẩm nào không đủ tồn kho, trả về null
        if (processedCartItems.size() < cartItems.size()) {
            return null;
        }

        // Tạo phản hồi
        CheckoutResponse response = new CheckoutResponse();
        response.setName(name);
        response.setPhone(phone);
        response.setEmail(email);
        response.setAddress(addressEntity);
        response.setCartItems(processedCartItems);
        response.setTotal(total.get());

        return response;
    }

    @Override
    public void setDelivering(Long id) {
        OrderEntity order = orderRepository.findById(id).get();
        order.setShippingStatus(OrderShippingStatus.DELIVERING);
        orderRepository.save(order);
    }

    @Override
    public void setDeliveried(Long id) {
        OrderEntity order = orderRepository.findById(id).get();
        order.setShippingStatus(OrderShippingStatus.DELIVERIED);
        orderRepository.save(order);
    }


    @Override
    public OrderResponse toOrderResponse(OrderEntity order) {
        List<LineItemResponse> items = order.getListLineItems().stream()
                .map(lineItem -> new LineItemResponse(
                        lineItem.getProduct().getName(),
                        lineItem.getProduct().getDetail().getImages().getFirst().getImageUrl(),
                        lineItem.getProduct().getPrice(),
                        lineItem.getQuantity(),
                        lineItem.getProduct().getPrice() * lineItem.getQuantity()
                ))
                .toList();

        double totalAmount = items.stream()
                .mapToDouble(LineItemResponse::getSubtotal)
                .sum();

        return new OrderResponse(
                order.getOrderId(),
                order.getUser().getName(),
                order.getPhoneNumber(),
                order.getShippingAddress(),
                order.getOrderDate(),
                order.getReceiveDate(),
                order.getShippingStatus().name(),
                order.getPaymentStatus().name(),
                order.getNote(),
                totalAmount,
                items
        );
    }

}
