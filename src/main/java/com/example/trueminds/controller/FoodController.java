package com.example.trueminds.controller;

import com.example.trueminds.dtos.FoodItemRequest;
import com.example.trueminds.dtos.FoodItemResponse;
import com.example.trueminds.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/foods")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @PostMapping
    public ResponseEntity<FoodItemResponse> createFood(@RequestParam String requesterId, @RequestBody FoodItemRequest request) {
        return ResponseEntity.ok(foodService.createFood(requesterId, request));
    }

    @PutMapping("/{foodId}")
    public ResponseEntity<FoodItemResponse> updateFood(@RequestParam String requesterId, @PathVariable String foodId, @RequestBody FoodItemRequest request) {
        return ResponseEntity.ok(foodService.updateFood(requesterId, foodId, request));
    }

    @DeleteMapping("/{foodId}")
    public ResponseEntity<Void> deleteFood(@RequestParam String requesterId, @PathVariable String foodId) {
        foodService.deleteFood(requesterId, foodId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/admin/all")
    public ResponseEntity<List<FoodItemResponse>> getAllFoodsForAdmin(@RequestParam String requesterId) {
        return ResponseEntity.ok(foodService.getAllFoodsForAdmin(requesterId));
    }

    @GetMapping
    public ResponseEntity<List<FoodItemResponse>> getAvailableFoods() {
        return ResponseEntity.ok(foodService.getAvailableFoods());
    }
}