package com.workstudy.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.workstudy.backend.model.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findByStudentId(Long studentId);

    Optional<Certificate> findByVerifyId(String verifyId);

    long countByStudentId(Long studentId);

    @org.springframework.data.jpa.repository.Modifying
    @jakarta.transaction.Transactional
    @org.springframework.data.jpa.repository.Query("DELETE FROM Certificate c WHERE c.student.id = :studentId")
    void deleteByStudentId(Long studentId);
}
