package com.example.trueminds.mappers;

import com.example.trueminds.dtos.FoodItemRequest;
import com.example.trueminds.dtos.FoodItemResponse;
import com.example.trueminds.model.FoodItem;
import org.springframework.stereotype.Component;

@Component
public class FoodMapper {

    public FoodItem toEntity(FoodItemRequest request) {
        FoodItem item = new FoodItem();
        item.setName(request.name());
        item.setDescription(request.description());
        item.setPrice(request.price());
        item.setImageUrl(request.imageUrl());
        item.setQuantity(request.quantity());
        return item;
    }

    public FoodItemResponse toResponse(FoodItem item) {
        return new FoodItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.isAvailable(),
                item.getImageUrl(),
                item.getQuantity()
        );
    }
}