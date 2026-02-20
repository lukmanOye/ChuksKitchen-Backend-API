package com.example.trueminds.dtos;

import com.example.trueminds.enums.OrderStatus;

public record OrderStatusUpdateRequest(OrderStatus status) {}