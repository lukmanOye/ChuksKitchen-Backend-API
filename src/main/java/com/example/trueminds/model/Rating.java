package com.example.trueminds.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Rating {
    private String id;
    private String userId;
    private String orderId;
    private int stars;
    private String comment;
    private LocalDateTime createdAt;

    public Rating() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }
}