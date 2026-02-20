package com.example.trueminds.dtos;

public record RatingRequest(String orderId, int stars, String comment) {}