package com.example.trueminds.dtos;

public record UpdateUserRequest(
        String oldEmail,
        String newEmail,
        String newPassword,
        String newPhone,
        String newAddress
) {}