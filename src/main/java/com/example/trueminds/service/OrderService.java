package com.example.trueminds.service;

import com.example.trueminds.dtos.*;
import com.example.trueminds.enums.OrderStatus;
import com.example.trueminds.enums.Role;
import com.example.trueminds.model.*;
import com.example.trueminds.repository.CartRepository;
import com.example.trueminds.repository.FoodRepository;
import com.example.trueminds.repository.OrderRepository;
import com.example.trueminds.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private userRepository userRepository;
    @Autowired
    private CartRepository cartRepository;

    private void checkAdmin(String requesterId) {
        user user = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester not found"));
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Admin only");
        }
    }

    private void checkUserVerified(String userId) {
        user user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isVerified()) {
            throw new RuntimeException("User not verified. Please verify your account first.");
        }
    }

    public OrderResponse placeOrder(String userId) {
        checkUserVerified(userId);
        user user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getUserAddress() == null || user.getUserAddress().trim().isEmpty()) {
            throw new RuntimeException("Please provide a delivery address before placing an order.");
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart is empty"));

        List<orderItems> orderItems = new ArrayList<>();
        double total = 0.0;

        for (CartItem cartItem : cart.getItems()) {
            FoodItem food = foodRepository.findById(cartItem.foodId())
                    .orElseThrow(() -> new RuntimeException("Food not found: " + cartItem.foodId()));

            if (!food.isAvailable()) {
                throw new RuntimeException("Food item no longer available: " + food.getName());
            }

            orderItems.add(new orderItems(
                    food.getId(),
                    food.getImageUrl(),
                    food.getName(),
                    cartItem.quantity(),
                    food.getPrice()
            ));
            total += food.getPrice() * cartItem.quantity();
        }

        order order = new order();
        order.setUserId(userId);
        order.setItems(orderItems);
        order.setTotal(total);
        order.setStatus(OrderStatus.PENDING);

        order saved = orderRepository.save(order);
        cartRepository.deleteByUserId(userId);

        return mapToResponse(saved);
    }

    public OrderResponse payOrder(String userId, String orderId) {
        checkUserVerified(userId);
        order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied: Order does not belong to you");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order cannot be paid – current status: " + order.getStatus());
        }

        user user = userRepository.findById(userId).orElseThrow();

        double balance = user.getBalance();
        double total = order.getTotal();
        if (balance < total - 0.0001) {
            throw new RuntimeException("Insufficient balance. Your balance: " + balance + ", order total: " + total);
        }

        user.setBalance(balance - total);
        userRepository.save(user);

        order.setStatus(OrderStatus.PAID);
        order updated = orderRepository.save(order);

        return mapToResponse(updated);
    }

    public void cancelOrder(String requesterId, String orderId) {
        order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        user requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester not found"));

        boolean isAdmin = requester.getRole() == Role.ADMIN;
        boolean isOwner = order.getUserId().equals(requesterId);

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("Access denied: You cannot cancel this order");
        }

        if (!isAdmin && order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("You can only cancel orders with status PENDING");
        }

        if (order.getStatus() == OrderStatus.PAID) {
            user orderOwner = userRepository.findById(order.getUserId()).orElseThrow();
            orderOwner.setBalance(orderOwner.getBalance() + order.getTotal());
            userRepository.save(orderOwner);
        }

        for (orderItems item : order.getItems()) {
            FoodItem food = foodRepository.findById(item.foodId())
                    .orElseThrow(() -> new RuntimeException("Food not found: " + item.foodId()));
            food.setQuantity(food.getQuantity() + item.quantity());
            foodRepository.save(food);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    public OrderResponse updateOrderStatus(String requesterId, String orderId, OrderStatusUpdateRequest request) {
        checkAdmin(requesterId);
        order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(request.status());
        order updated = orderRepository.save(order);
        return mapToResponse(updated);
    }

    public List<OrderResponse> getUserOrders(String userId) {
        checkUserVerified(userId);
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrder(String orderId, String userId) {
        checkUserVerified(userId);
        order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied: Order does not belong to user");
        }
        return mapToResponse(order);
    }

    public List<OrderResponse> getAllOrders(String requesterId) {
        checkAdmin(requesterId);
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToResponse(order order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getItems(),
                order.getTotal(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}