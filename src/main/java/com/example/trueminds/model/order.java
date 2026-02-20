package com.example.trueminds.model;

import com.example.trueminds.enums.OrderStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data

public class order {
    private String id;
    private String userId;
    private List<orderItems> items;
    private double total;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public order() {
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }
}