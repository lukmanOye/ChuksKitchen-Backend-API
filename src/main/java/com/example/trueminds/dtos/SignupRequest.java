package com.example.trueminds.dtos;


import com.example.trueminds.enums.Role;

public record SignupRequest (
        String username,
        String email,
        String phone,
        String password,
        String referralCode,
        String address,
        Role role){


}
