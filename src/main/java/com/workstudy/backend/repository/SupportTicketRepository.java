package com.workstudy.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.workstudy.backend.model.SupportTicket;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    List<SupportTicket> findByStudentId(Long studentId);

    List<SupportTicket> findByStatus(String status);

    List<SupportTicket> findByPriority(String priority);

    List<SupportTicket> findByCategory(String category);

    List<SupportTicket> findByAssignedAdmin(String adminName);

    long countByStatus(String status);

    @org.springframework.data.jpa.repository.Modifying
    @jakarta.transaction.Transactional
    @Query("DELETE FROM SupportTicket t WHERE t.student.id = :studentId")
    void deleteByStudentId(Long studentId);
}
