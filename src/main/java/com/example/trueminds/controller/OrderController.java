package com.example.trueminds.controller;

import com.example.trueminds.dtos.OrderResponse;
import com.example.trueminds.dtos.OrderStatusUpdateRequest;
import com.example.trueminds.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place/{userId}")
    public ResponseEntity<OrderResponse> placeOrder(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.placeOrder(userId));
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId, @RequestParam String userId) {
        return ResponseEntity.ok(orderService.getOrder(orderId, userId));
    }
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<OrderResponse> payOrder(@RequestParam String userId, @PathVariable String orderId) {
        OrderResponse response = orderService.payOrder(userId, orderId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@RequestParam String requesterId, @PathVariable String orderId) {
        orderService.cancelOrder(requesterId, orderId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/admin/all")
    public ResponseEntity<List<OrderResponse>> getAllOrders(@RequestParam String requesterId) {
        return ResponseEntity.ok(orderService.getAllOrders(requesterId));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(@RequestParam String requesterId, @PathVariable String orderId, @RequestBody OrderStatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(requesterId, orderId, request));
    }
}