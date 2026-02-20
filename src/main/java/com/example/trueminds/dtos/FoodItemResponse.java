package com.example.trueminds.dtos;

public record FoodItemResponse(String id, String name, String description, double price, boolean available, String imageUrl, int quantity) {}