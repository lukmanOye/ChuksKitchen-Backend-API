package com.example.trueminds.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class FoodItem {
    private String id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String imageUrl;
    private LocalDateTime createdAt;

    public FoodItem() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.quantity = 0;
    }

    public boolean isAvailable() {
        return quantity > 0;
    }
}