package com.example.trueminds.service;

import com.example.trueminds.dtos.RatingRequest;
import com.example.trueminds.dtos.RatingResponse;
import com.example.trueminds.enums.Role;
import com.example.trueminds.mappers.RatingMapper;
import com.example.trueminds.model.Rating;
import com.example.trueminds.model.user;
import com.example.trueminds.repository.RatingRepository;
import com.example.trueminds.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private userRepository userRepository;
    @Autowired
    private RatingMapper ratingMapper;

    private void checkAdmin(String requesterId) {
        user user = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester not found"));
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Admin only");
        }
    }

    private void checkUserVerified(String userId) {
        user user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.isVerified()) {
            throw new RuntimeException("User not verified");
        }
    }

    public RatingResponse createRating(String userId, RatingRequest request) {
        checkUserVerified(userId);
        Rating rating = ratingMapper.toEntity(request, userId);
        Rating saved = ratingRepository.save(rating);
        return ratingMapper.toResponse(saved);
    }

    public List<RatingResponse> getAllRatings(String requesterId) {
        checkAdmin(requesterId);
        return ratingRepository.findAll().stream()
                .map(ratingMapper::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteRating(String requesterId, String ratingId) {
        checkAdmin(requesterId);
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Rating not found"));
        ratingRepository.deleteById(ratingId);
    }

    public List<RatingResponse> getRatingsByUser(String userId) {
        return ratingRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId))
                .map(ratingMapper::toResponse)
                .collect(Collectors.toList());
    }
}