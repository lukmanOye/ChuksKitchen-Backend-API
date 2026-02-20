package com.example.trueminds.dtos;

import com.example.trueminds.enums.Role;

import java.time.LocalDateTime;


public record UserResponse(String id,
        String username,
        String email,
        String phone,
        boolean verified,
        LocalDateTime createdAt,
        ReferralCodeDto referralCode,
        Role role,
        String addresses,double balance){

}