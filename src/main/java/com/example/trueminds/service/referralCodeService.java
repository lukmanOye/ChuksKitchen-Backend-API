package com.example.trueminds.service;

import com.example.trueminds.dtos.ReferralCodeDto;
import com.example.trueminds.enums.Role;
import com.example.trueminds.model.referralCode;
import com.example.trueminds.model.user;
import com.example.trueminds.repository.referralCodeRepository;
import com.example.trueminds.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class referralCodeService {

    @Autowired
    private referralCodeRepository referralCodeRepository;
    @Autowired
    private userRepository userRepository;

    private void checkAdmin(String requesterId) {
        user user = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester not found"));
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied: Admin only");
        }
    }

    public ReferralCodeDto createReferralCode(String requesterId, String name) {
        checkAdmin(requesterId);
        if (referralCodeRepository.findByName(name).isPresent()) {
            throw new RuntimeException("Referral code already exists");
        }
        long newId = System.currentTimeMillis();
        referralCode code = new referralCode(newId, name);
        referralCode saved = referralCodeRepository.save(code);
        return new ReferralCodeDto(saved.id(), saved.name());
    }

    public void deleteReferralCode(String requesterId, String name) {
        checkAdmin(requesterId);
        referralCodeRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Referral code not found"));
        referralCodeRepository.deleteByName(name);
    }

    public List<ReferralCodeDto> getAllReferralCodes(String requesterId) {
        checkAdmin(requesterId);
        return referralCodeRepository.findAll().stream()
                .map(code -> new ReferralCodeDto(code.id(), code.name()))
                .collect(Collectors.toList());
    }
}