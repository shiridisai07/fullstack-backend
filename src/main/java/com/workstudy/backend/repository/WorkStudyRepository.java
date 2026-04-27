package com.workstudy.backend.repository;

import com.workstudy.backend.model.WorkStudy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkStudyRepository extends JpaRepository<WorkStudy, Long> {
    List<WorkStudy> findByStudentId(Long studentId);
    List<WorkStudy> findByJobId(Long jobId);
    List<WorkStudy> findByStatus(String status);
    void deleteByStudentId(Long studentId);
}
