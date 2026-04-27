package com.workstudy.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.workstudy.backend.model.SavedJob;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    List<SavedJob> findByStudentId(Long studentId);

    boolean existsByStudentIdAndJobId(Long studentId, Long jobId);

    @org.springframework.data.jpa.repository.Modifying
    @jakarta.transaction.Transactional
    @org.springframework.data.jpa.repository.Query("DELETE FROM SavedJob s WHERE s.student.id = :studentId AND s.job.id = :jobId")
    void deleteByStudentIdAndJobId(Long studentId, Long jobId);

    @org.springframework.data.jpa.repository.Modifying
    @jakarta.transaction.Transactional
    @org.springframework.data.jpa.repository.Query("DELETE FROM SavedJob s WHERE s.student.id = :studentId")
    void deleteByStudentId(Long studentId);

    @org.springframework.data.jpa.repository.Modifying
    @jakarta.transaction.Transactional
    @org.springframework.data.jpa.repository.Query("DELETE FROM SavedJob s WHERE s.job.id = :jobId")
    void deleteByJobId(Long jobId);
}
