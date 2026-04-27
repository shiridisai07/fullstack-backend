package com.workstudy.backend.model;

import jakarta.persistence.*;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;

    private String role;
    private String authProvider;
    private String mfaCode;
    
    private String masterResumePath;

    // Profile & Portfolio fields
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String skills;

    private String githubUrl;
    private String linkedinUrl;
    private String portfolioUrl;

    private boolean blocked;
    private String status;

    public String getAuthProvider() {
        return authProvider;
    }
    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public String getMfaCode() {
        return mfaCode;
    }
    public void setMfaCode(String mfaCode) {
        this.mfaCode = mfaCode;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getMasterResumePath() {
        return masterResumePath;
    }
    public void setMasterResumePath(String masterResumePath) {
        this.masterResumePath = masterResumePath;
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String githubUrl) { this.githubUrl = githubUrl; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }

    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
