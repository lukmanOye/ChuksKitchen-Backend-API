package com.example.trueminds.service;

import com.example.trueminds.dtos.CartItemRequest;
import com.example.trueminds.dtos.CartResponse;
import com.example.trueminds.model.Cart;
import com.example.trueminds.model.CartItem;
import com.example.trueminds.model.FoodItem;
import com.example.trueminds.model.user;
import com.example.trueminds.repository.CartRepository;
import com.example.trueminds.repository.FoodRepository;
import com.example.trueminds.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private userRepository userRepository;

    private void checkUserVerified(String userId) {
        user user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isVerified()) {
            throw new RuntimeException("User not verified. Please verify your account first.");
        }
    }

    private void adjustStock(String foodId, int delta) {
        FoodItem food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food not found"));
        food.setQuantity(food.getQuantity() + delta);
        foodRepository.save(food);
    }

    public CartResponse addToCart(String userId, CartItemRequest request) {
        checkUserVerified(userId);
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        FoodItem food = foodRepository.findById(request.foodId())
                .orElseThrow(() -> new RuntimeException("Food not found"));

        if (!food.isAvailable()) {
            throw new RuntimeException("Food item not available: " + food.getName());
        }

        Cart cart = cartRepository.findByUserId(userId).orElse(new Cart());
        cart.setUserId(userId);

        List<CartItem> currentItems = cart.getItems();
        List<CartItem> updatedItems = new ArrayList<>();
        boolean found = false;

        for (CartItem item : currentItems) {
            if (item.foodId().equals(request.foodId())) {
                // Item already in cart – adjust stock by difference
                int oldQty = item.quantity();
                int newQty = request.quantity();
                int delta = newQty - oldQty;
                if (delta > 0) {
                    // Need extra stock
                    if (food.getQuantity() < delta) {
                        throw new RuntimeException("Insufficient stock for " + food.getName() +
                                ". Available: " + food.getQuantity() + ", needed extra: " + delta);
                    }
                    adjustStock(food.getId(), -delta);
                } else if (delta < 0) {
                    // Return stock
                    adjustStock(food.getId(), -delta); // delta negative, so adding back
                }
                // Replace with new item (same price snapshot, but we keep current price for simplicity)
                updatedItems.add(new CartItem(food.getId(), food.getName(), newQty, food.getPrice()));
                found = true;
            } else {
                updatedItems.add(item);
            }
        }

        if (!found) {
            // New item
            if (food.getQuantity() < request.quantity()) {
                throw new RuntimeException("Insufficient stock for " + food.getName() +
                        ". Available: " + food.getQuantity());
            }
            adjustStock(food.getId(), -request.quantity());
            updatedItems.add(new CartItem(food.getId(), food.getName(), request.quantity(), food.getPrice()));
        }

        cart.setItems(updatedItems);
        Cart saved = cartRepository.save(cart);
        return mapToResponse(saved);
    }

    public CartResponse removeFromCart(String userId, String foodId) {
        checkUserVerified(userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItem> updated = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            if (item.foodId().equals(foodId)) {
                // Restore stock
                adjustStock(foodId, item.quantity());
            } else {
                updated.add(item);
            }
        }
        cart.setItems(updated);
        if (updated.isEmpty()) {
            cartRepository.deleteByUserId(userId);
            return new CartResponse(userId, List.of(), 0.0);
        }
        Cart saved = cartRepository.save(cart);
        return mapToResponse(saved);
    }

    public CartResponse viewCart(String userId) {
        checkUserVerified(userId);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart is empty"));
        return mapToResponse(cart);
    }

    public void clearCart(String userId) {
        checkUserVerified(userId);
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart != null) {
            // Restore stock for all items
            for (CartItem item : cart.getItems()) {
                adjustStock(item.foodId(), item.quantity());
            }
            cartRepository.deleteByUserId(userId);
        }
    }

    private CartResponse mapToResponse(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(item -> item.price() * item.quantity())
                .sum();
        return new CartResponse(cart.getUserId(), cart.getItems(), total);
    }
}