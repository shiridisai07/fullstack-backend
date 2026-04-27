package com.workstudy.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.workstudy.backend.model.Certificate;
import com.workstudy.backend.repository.CertificateRepository;

@RestController
@RequestMapping("/api/certificates")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@SuppressWarnings("null")
public class CertificateController {

    @Autowired
    private CertificateRepository certificateRepository;

    @GetMapping("/student/{studentId}")
    public List<Certificate> getByStudent(@PathVariable Long studentId) {
        return certificateRepository.findByStudentId(studentId);
    }

    @GetMapping("/verify/{verifyId}")
    public Certificate verify(@PathVariable String verifyId) {
        return certificateRepository.findByVerifyId(verifyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not found"));
    }
}
