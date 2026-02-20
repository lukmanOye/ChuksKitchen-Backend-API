package com.example.trueminds.dtos;

import com.example.trueminds.enums.OrderStatus;
import com.example.trueminds.model.orderItems;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String id,
        String userId,
        List<orderItems> items,   // ✅ use the same type as in the entity
        double total,
        OrderStatus status,
        LocalDateTime createdAt
) {}