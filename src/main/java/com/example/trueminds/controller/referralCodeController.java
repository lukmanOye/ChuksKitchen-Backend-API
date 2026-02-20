package com.example.trueminds.controller;

import com.example.trueminds.dtos.ReferralCodeDto;
import com.example.trueminds.service.referralCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/referral-codes")
public class referralCodeController {

    @Autowired
    private referralCodeService referralCodeService;

    @PostMapping
    public ResponseEntity<ReferralCodeDto> createCode(
            @RequestParam String requesterId,
            @RequestParam String name) {
        ReferralCodeDto dto = referralCodeService.createReferralCode(requesterId, name);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteCode(
            @RequestParam String requesterId,
            @PathVariable String name) {
        referralCodeService.deleteReferralCode(requesterId, name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ReferralCodeDto>> getAllCodes(@RequestParam String requesterId) {
        List<ReferralCodeDto> codes = referralCodeService.getAllReferralCodes(requesterId);
        return ResponseEntity.ok(codes);
    }
}