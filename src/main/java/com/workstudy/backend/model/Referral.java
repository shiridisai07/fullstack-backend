package com.workstudy.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "referrals")
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student referrer;

    private String referredEmail;
    private String status;
    private Double rewardAmount;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "PENDING";
        if (this.rewardAmount == null) this.rewardAmount = 0.0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getReferrer() { return referrer; }
    public void setReferrer(Student referrer) { this.referrer = referrer; }

    public String getReferredEmail() { return referredEmail; }
    public void setReferredEmail(String referredEmail) { this.referredEmail = referredEmail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getRewardAmount() { return rewardAmount; }
    public void setRewardAmount(Double rewardAmount) { this.rewardAmount = rewardAmount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
