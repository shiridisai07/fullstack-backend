package com.workstudy.backend.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.workstudy.backend.model.Referral;
import com.workstudy.backend.model.Student;
import com.workstudy.backend.repository.ReferralRepository;
import com.workstudy.backend.repository.StudentRepository;

@RestController
@RequestMapping("/api/referrals")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@SuppressWarnings("null")
public class ReferralController {

    @Autowired
    private ReferralRepository referralRepository;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping
    public Referral create(@RequestBody Map<String, String> req) {
        Long referrerId = Long.parseLong(req.get("referrerId"));
        String referredEmail = req.get("referredEmail");

        Student referrer = studentRepository.findById(referrerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Referrer not found"));

        if (referralRepository.existsByReferrerIdAndReferredEmail(referrerId, referredEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Referral already exists for this email");
        }

        Referral ref = new Referral();
        ref.setReferrer(referrer);
        ref.setReferredEmail(referredEmail);
        return referralRepository.save(ref);
    }

    @GetMapping("/student/{studentId}")
    public List<Referral> getByStudent(@PathVariable Long studentId) {
        return referralRepository.findByReferrerId(studentId);
    }

    @GetMapping("/student/{studentId}/stats")
    public ResponseEntity<?> getStats(@PathVariable Long studentId) {
        List<Referral> refs = referralRepository.findByReferrerId(studentId);
        long signedUp = referralRepository.countByReferrerIdAndStatus(studentId, "SIGNED_UP");
        double totalEarned = refs.stream().mapToDouble(Referral::getRewardAmount).sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalInvited", refs.size());
        stats.put("signedUp", signedUp);
        stats.put("totalEarned", totalEarned);
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/{id}")
    public Referral updateStatus(@PathVariable Long id, @RequestParam String status) {
        Referral ref = referralRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Referral not found"));
        ref.setStatus(status);
        if ("SIGNED_UP".equals(status)) {
            ref.setRewardAmount(5.0);
        }
        return referralRepository.save(ref);
    }
}
