package com.workstudy.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.workstudy.backend.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    List<Notification> findByStudentIdAndReadFalseOrderByCreatedAtDesc(Long studentId);

    long countByStudentIdAndReadFalse(Long studentId);

    @org.springframework.data.jpa.repository.Modifying
    @jakarta.transaction.Transactional
    @org.springframework.data.jpa.repository.Query("UPDATE Notification n SET n.read = true WHERE n.student.id = :studentId")
    void markAllReadByStudentId(Long studentId);

    @org.springframework.data.jpa.repository.Modifying
    @jakarta.transaction.Transactional
    @org.springframework.data.jpa.repository.Query("DELETE FROM Notification n WHERE n.student.id = :studentId")
    void deleteByStudentId(Long studentId);
}
