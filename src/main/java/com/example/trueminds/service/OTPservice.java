package com.example.trueminds.service;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OTPservice {
    private static final long OTP_EXPIRY_SECONDS = 300;

    // Store OTP along with creation time
    private final Map<String, OtpRecord> otpStorage = new ConcurrentHashMap<>();

    private record OtpRecord(String otp, Instant createdAt) {}

    public String generateOTP(String email) {
        String otp = String.valueOf((int)(Math.random() * 900000) + 100000);
        otpStorage.put(email, new OtpRecord(otp, Instant.now()));
        System.out.println("OTP for " + email + ": " + otp);
        return otp;
    }

    public boolean verifyOTP(String email, String otp) {
        OtpRecord record = otpStorage.get(email);
        if (record == null) return false;
        if (Instant.now().isAfter(record.createdAt().plusSeconds(OTP_EXPIRY_SECONDS))) {
            otpStorage.remove(email);
            return false;
        }
        if (record.otp().equals(otp)) {
            otpStorage.remove(email);
            return true;
        }
        return false;
    }

}