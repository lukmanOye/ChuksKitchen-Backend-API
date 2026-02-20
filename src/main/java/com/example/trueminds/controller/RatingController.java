package com.example.trueminds.controller;

import com.example.trueminds.dtos.RatingRequest;
import com.example.trueminds.dtos.RatingResponse;
import com.example.trueminds.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;


    @PostMapping
    public ResponseEntity<RatingResponse> createRating(@RequestParam String userId, @RequestBody RatingRequest request) {
        RatingResponse response = ratingService.createRating(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<RatingResponse>> getAllRatings(@RequestParam String requesterId) {
        List<RatingResponse> ratings = ratingService.getAllRatings(requesterId);
        return ResponseEntity.ok(ratings);
    }

    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> deleteRating(@RequestParam String requesterId, @PathVariable String ratingId) {
        ratingService.deleteRating(requesterId, ratingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RatingResponse>> getRatingsByUser(@PathVariable String userId) {
        List<RatingResponse> ratings = ratingService.getRatingsByUser(userId);
        return ResponseEntity.ok(ratings);
    }
}