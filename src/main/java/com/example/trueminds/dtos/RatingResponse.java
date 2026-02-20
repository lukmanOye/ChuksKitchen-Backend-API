package com.example.trueminds.dtos;

import java.time.LocalDateTime;

public record RatingResponse(String id, String userId, String orderId, int stars, String comment, LocalDateTime createdAt) {}