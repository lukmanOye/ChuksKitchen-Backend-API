package com.example.trueminds.dtos;

public record FoodItemRequest(String name, String description, double price, String imageUrl, int quantity) {}