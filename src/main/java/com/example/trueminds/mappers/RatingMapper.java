package com.example.trueminds.mappers;

import com.example.trueminds.dtos.RatingRequest;
import com.example.trueminds.dtos.RatingResponse;
import com.example.trueminds.model.Rating;
import org.springframework.stereotype.Component;

@Component
public class RatingMapper {
    public Rating toEntity(RatingRequest request, String userId) {
        Rating rating = new Rating();
        rating.setUserId(userId);
        rating.setOrderId(request.orderId());
        rating.setStars(request.stars());
        rating.setComment(request.comment());
        return rating;
    }

    public RatingResponse toResponse(Rating rating) {
        return new RatingResponse(
                rating.getId(),
                rating.getUserId(),
                rating.getOrderId(),
                rating.getStars(),
                rating.getComment(),
                rating.getCreatedAt()
        );
    }
}