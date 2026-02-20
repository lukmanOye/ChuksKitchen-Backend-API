package com.example.trueminds.service;

import com.example.trueminds.dtos.*;
import com.example.trueminds.enums.Role;
import com.example.trueminds.mappers.userMapper;
import com.example.trueminds.model.referralCode;
import com.example.trueminds.model.user;
import com.example.trueminds.repository.referralCodeRepository;
import com.example.trueminds.repository.userRepository;
import com.example.trueminds.util.LuhnValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class userService {

    @Autowired
    private userRepository userRepository;
    @Autowired
    private referralCodeRepository referralCodeRepository;
    @Autowired
    private OTPservice otpService;
    @Autowired
    private userMapper userMapper;

    public UserResponse signup(SignupRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        if (userRepository.findByPhone(request.phone()).isPresent()) {
            throw new RuntimeException("Phone number already registered");
        }

        referralCode refCode = null;
        if (request.referralCode() != null && !request.referralCode().isEmpty()) {
            refCode = referralCodeRepository.findByName(request.referralCode())
                    .orElseThrow(() -> new RuntimeException("Invalid referral code"));
        }

        user user = userMapper.toEntity(request);
        user.setReferralCode(refCode);
        user.setVerified(false);

        user savedUser = userRepository.save(user);

        otpService.generateOTP(savedUser.getEmail());

        return userMapper.toResponse(savedUser);
    }

    public UserResponse login(LoginRequest request) {
        user user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!request.password().equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (!otpService.verifyOTP(request.email(), request.otp())) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        if (!user.isVerified()) {
            user.setVerified(true);
            userRepository.save(user);
        }

        return userMapper.toResponse(user);
    }

    public UserResponse verify(VerifyRequest request) {
        user user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!otpService.verifyOTP(request.email(), request.otp())) {
            throw new RuntimeException("Invalid or expired OTP");
        }
        user.setVerified(true);
        userRepository.save(user);

        return userMapper.toResponse(user);
    }

    public String resendOtp(String email) {
        if (userRepository.findByEmail(email).isEmpty()) {
            throw new RuntimeException("User not found");
        }
        otpService.generateOTP(email);
        return "New OTP sent" ;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(String id) {
        user user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return userMapper.toResponse(user);
    }

    public UserResponse getUserByEmail(String email) {
        user user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    public void deleteUser(String requesterId, String targetUserId) {
        checkAdmin(requesterId);

        if (!userRepository.findById(targetUserId).isPresent()) {
            throw new RuntimeException("User not found with id: " + targetUserId);
        }
        userRepository.deleteById(targetUserId);
    }

    private void checkAdmin(String userId) {
        user user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Requester not found"));
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Admin only");
        }
    }

    public UserResponse updateUser(String requesterId, String targetUserId, UpdateUserRequest request) {

        user requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester not found"));
        if (!requester.getRole().equals(Role.ADMIN) && !requesterId.equals(targetUserId)) {
            throw new RuntimeException("Access denied: You can only update your own account");
        }

        user targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + targetUserId));

        boolean updated = false;

        if (request.oldEmail() != null && !request.oldEmail().isEmpty() &&
                request.newEmail() != null && !request.newEmail().isEmpty()) {

            if (!request.oldEmail().equals(targetUser.getEmail())) {
                throw new RuntimeException("Old email does not match current email");
            }
            userRepository.findByEmail(request.newEmail()).ifPresent(existing -> {
                if (!existing.getId().equals(targetUserId)) {
                    throw new RuntimeException("New email is already in use by another user");
                }
            });
            targetUser.setEmail(request.newEmail());
            updated = true;
        }

        if (request.newPassword() != null && !request.newPassword().isEmpty()) {
            targetUser.setPassword(request.newPassword());
            updated = true;
        }


        if (request.newPhone() != null && !request.newPhone().isEmpty()) {
            userRepository.findByPhone(request.newPhone()).ifPresent(existing -> {
                if (!existing.getId().equals(targetUserId)) {
                    throw new RuntimeException("Phone number is already in use by another user");
                }
            });
            targetUser.setPhone(request.newPhone());
            updated = true;
        }

        // Address change
        if (request.newAddress() != null && !request.newAddress().isEmpty()) {
            targetUser.setUserAddress(request.newAddress());
            updated = true;
        }

        if (!updated) {
            throw new RuntimeException("No valid update fields provided");
        }

        user savedUser = userRepository.save(targetUser);
        return userMapper.toResponse(savedUser);
    }

    public void topUpBalance(String userId, TopUpRequest request) {
        user user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!LuhnValidator.isValid(request.cardNumber())) {
            throw new RuntimeException("Invalid card number");
        }

        user.setBalance(user.getBalance() + request.amount());
        userRepository.save(user);
    }
}