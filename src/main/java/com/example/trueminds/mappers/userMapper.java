package com.example.trueminds.mappers;

import com.example.trueminds.dtos.ReferralCodeDto;
import com.example.trueminds.dtos.SignupRequest;
import com.example.trueminds.dtos.UserResponse;
import com.example.trueminds.model.user;
import com.example.trueminds.model.referralCode;
import org.springframework.stereotype.Component;



@Component
public class userMapper {


    public UserResponse toResponse(user user) {
        ReferralCodeDto referralCodeDto = null;
        if (user.getReferralCode() != null) {
            referralCodeDto = toReferralCodeDto(user.getReferralCode());
        }


        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.isVerified(),
                user.getCreatedAt(),
                referralCodeDto,
                user.getRole(),
                user.getUserAddress(),
                user.getBalance()
        );
    }
    public user toEntity(SignupRequest request) {
        user user = new user();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setPassword(request.password());
        user.setUserAddress(request.address());
        if (request.role() !=null){
            user.setRole(request.role());
        }
        return user;
    }


    public ReferralCodeDto toReferralCodeDto(referralCode ref) {
        if (ref == null) return null;
        return new ReferralCodeDto(ref.id(), ref.name());
    }


}