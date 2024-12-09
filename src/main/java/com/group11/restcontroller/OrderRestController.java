package com.group11.restcontroller;

import com.group11.dto.request.LineItemRequest;
import com.group11.dto.response.LineItemResponse;
import com.group11.dto.response.OrderResponse;
import com.group11.entity.*;
import com.group11.service.IJwtService;
import com.group11.service.IOrderService;
import com.group11.service.IProductService;
import com.group11.dto.response.CheckoutResponse;

import com.group11.service.IUserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {
    @Autowired
    IUserService userService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private IJwtService jwtService;
    @Autowired
    private IProductService productService;

    @GetMapping
    public Page<OrderResponse> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return orderService.getAllOrders(page, size);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping("/delivering/{id}")
    public ResponseEntity<String> setDelivering(@PathVariable Long id) {
        orderService.setDelivering(id);
        return ResponseEntity.ok("Xác nhận đơn đang giao thành công");
    }

    @PostMapping("/deliveried/{id}")
    public ResponseEntity<String> setDeliveried(@PathVariable Long id) {
        orderService.setDeliveried(id);
        return ResponseEntity.ok("Xác nhận đơn đã giao thành công");
    }


    @GetMapping("/user/{id}")
    public Page<OrderResponse> getOrdersByUserId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return orderService.getAllOrdersByUserId(id, page, size);
    }

    @GetMapping("/search/{keyword}")
    public Page<OrderResponse> searchOrders(@PathVariable String keyword,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return orderService.searchOrders(keyword, page, size);
    }


    @PostMapping("/checkout")
    public ResponseEntity<Object> checkoutOrders(@RequestHeader("Authorization") String token,
                                                                @RequestBody List<LineItemRequest> cartItems){
        CheckoutResponse checkoutResponse = orderService.checkOutOrder(token, cartItems);
        if(checkoutResponse == null){
            return ResponseEntity.badRequest().body("Không đủ số lượng");
        }
        return ResponseEntity.ok(checkoutResponse);
    }

    @PostMapping("/create")
    public ResponseEntity<Object>  createOrder(@RequestHeader("Authorization") String token,
            @RequestBody CheckoutResponse response) {
        OrderEntity order = orderService.createOrder(token, response);
        if (order == null) {
            return ResponseEntity.badRequest().body("Đặt hàng không thành công. Vui lòng thử lại!");
        }
        return ResponseEntity.ok(order);
    }

}

