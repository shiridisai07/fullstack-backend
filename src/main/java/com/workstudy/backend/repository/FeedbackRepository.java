package com.workstudy.backend.repository;

import com.workstudy.backend.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByStudentId(Long studentId);
    List<Feedback> findByJobId(Long jobId);
    void deleteByStudentId(Long studentId);
}
