package com.example.trueminds.service;

import com.example.trueminds.dtos.FoodItemRequest;
import com.example.trueminds.dtos.FoodItemResponse;
import com.example.trueminds.mappers.FoodMapper;
import com.example.trueminds.model.FoodItem;
import com.example.trueminds.model.user;
import com.example.trueminds.repository.FoodRepository;
import com.example.trueminds.repository.userRepository;
import com.example.trueminds.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;
    @Autowired
    private userRepository userRepository;
    @Autowired
    private FoodMapper foodMapper;

    private void checkAdmin(String requesterId) {
        user user = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester not found"));
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Admin only");
        }
    }

    public FoodItemResponse createFood(String requesterId, FoodItemRequest request) {
        checkAdmin(requesterId);
        FoodItem item = foodMapper.toEntity(request);
        FoodItem saved = foodRepository.save(item);
        return foodMapper.toResponse(saved);
    }

    public FoodItemResponse updateFood(String requesterId, String foodId, FoodItemRequest request) {
        checkAdmin(requesterId);
        FoodItem item = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food not found"));
        item.setName(request.name());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setQuantity(request.quantity());
        item.setImageUrl(request.imageUrl());
        FoodItem updated = foodRepository.save(item);
        return foodMapper.toResponse(updated);
    }

    public void deleteFood(String requesterId, String foodId) {
        checkAdmin(requesterId);
        if (!foodRepository.findById(foodId).isPresent()) {
            throw new RuntimeException("Food not found");
        }
        foodRepository.deleteById(foodId);
    }



    public List<FoodItemResponse> getAllFoodsForAdmin(String requesterId) {
        checkAdmin(requesterId);
        return foodRepository.findAll().stream()
                .map(foodMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<FoodItemResponse> getAvailableFoods() {
        return foodRepository.findAvailable().stream()
                .map(foodMapper::toResponse)
                .collect(Collectors.toList());
    }
}