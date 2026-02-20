package com.example.trueminds.model;

public record orderItems(
        String foodId,
        String foodImage,
        String foodName,
        int quantity,
        double price
) {

}