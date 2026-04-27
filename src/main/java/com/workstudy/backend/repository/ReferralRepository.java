package com.workstudy.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.workstudy.backend.model.Referral;

public interface ReferralRepository extends JpaRepository<Referral, Long> {

    List<Referral> findByReferrerId(Long referrerId);

    boolean existsByReferrerIdAndReferredEmail(Long referrerId, String referredEmail);

    long countByReferrerIdAndStatus(Long referrerId, String status);

    @org.springframework.data.jpa.repository.Modifying
    @jakarta.transaction.Transactional
    @org.springframework.data.jpa.repository.Query("DELETE FROM Referral r WHERE r.referrer.id = :referrerId")
    void deleteByReferrerId(Long referrerId);
}
