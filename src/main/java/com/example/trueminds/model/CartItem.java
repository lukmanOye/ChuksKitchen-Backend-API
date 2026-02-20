package com.example.trueminds.model;

public record CartItem(
        String foodId,
        String foodName,
        int quantity,
        double price
) {}