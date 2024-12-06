package com.group11.restcontroller;


import com.group11.entity.OrderEntity;
import com.group11.entity.UserEntity;
import com.group11.service.IHistoryService;
import com.group11.service.IJwtService;
import com.group11.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class HistoryRestController {
    @Autowired
    private IHistoryService historyService;

    @Autowired
    private IJwtService jwtService;

    @Autowired
    private UserServiceImpl userService;

    // Phương thức GET để xem lịch sử đơn hàng
    @GetMapping
    public ResponseEntity<?> viewOrderHistory(@RequestHeader("Authorization") String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Bạn cần đăng nhập để xem lịch sử đơn hàng.");
        }

        token = token.substring(7); // Loại bỏ "Bearer " để lấy JWT thực tế

        String email = jwtService.extractClaim(token, claims -> claims.getSubject());
        UserEntity user = userService.findByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User không tồn tại.");
        }

        Long userID = user.getUserID();
        List<OrderEntity> orders = historyService.getPurchaseHistory(userID);

        if (orders.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("Bạn chưa mua đơn hàng nào.");
        }

        return ResponseEntity.ok(orders);
    }
    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(@RequestHeader("Authorization") String token,
                                         @PathVariable Long orderId,
                                         @RequestParam String accountNumber,
                                         @RequestParam String accountName,
                                         @RequestParam String bankName) {

        System.out.println("Cancel request received for orderId: " + orderId);
        System.out.println("Account details: " + accountNumber + ", " + accountName + ", " + bankName);


        token = token.substring(7); // Loại bỏ "Bearer " để lấy JWT thực tế
        String email = jwtService.extractClaim(token, claims -> claims.getSubject());
        UserEntity user = userService.findByEmail(email);



        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User không tồn tại.");
        }

        Long userID = user.getUserID();
        OrderEntity order = historyService.getOrderById(orderId);

        System.out.println("Order's User ID: " + order.getUser());
        System.out.println("Current User ID: " + userID);


        // Gọi phương thức hủy đơn hàng từ service
        historyService.cancelOrder(orderId, accountNumber, accountName, bankName);

        return ResponseEntity.ok("Đơn hàng đã được hủy thành công.");
    }
}
