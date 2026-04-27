package com.workstudy.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "workstudy")
public class WorkStudy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    private String semester; // e.g. "Fall 2026", "Spring 2026"

    private String status = "ACTIVE"; // ACTIVE, COMPLETED, TERMINATED

    private Double totalHoursAllowed = 20.0; // Max hours per week allowed

    private Double totalEarnings = 0.0;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getTotalHoursAllowed() { return totalHoursAllowed; }
    public void setTotalHoursAllowed(Double totalHoursAllowed) { this.totalHoursAllowed = totalHoursAllowed; }

    public Double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(Double totalEarnings) { this.totalEarnings = totalEarnings; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
