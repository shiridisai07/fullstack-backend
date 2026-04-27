package com.workstudy.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    private String title;
    private String courseName;
    private String status; // "Completed" or "In Progress"
    private String grade;
    private String issueDate;

    @Column(unique = true)
    private String verifyId;

    @PrePersist
    private void generateVerifyId() {
        if (verifyId == null || verifyId.isBlank()) {
            verifyId = "SB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (issueDate == null || issueDate.isBlank()) {
            issueDate = LocalDate.now().toString();
        }
        if (status == null || status.isBlank()) {
            status = "Completed";
        }
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }

    public String getVerifyId() { return verifyId; }
    public void setVerifyId(String verifyId) { this.verifyId = verifyId; }
}
