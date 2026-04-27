package com.workstudy.backend.controller;

import com.workstudy.backend.model.WorkStudy;
import com.workstudy.backend.model.Student;
import com.workstudy.backend.model.Job;
import com.workstudy.backend.repository.WorkStudyRepository;
import com.workstudy.backend.repository.StudentRepository;
import com.workstudy.backend.repository.JobRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workstudy")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@SuppressWarnings("null")
public class WorkStudyController {

    @Autowired
    private WorkStudyRepository workStudyRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JobRepository jobRepository;

    // Get all work-study assignments
    @GetMapping
    public List<WorkStudy> getAll() {
        return workStudyRepository.findAll();
    }

    // Get by student
    @GetMapping("/student/{studentId}")
    public List<WorkStudy> getByStudent(@PathVariable Long studentId) {
        return workStudyRepository.findByStudentId(studentId);
    }

    // Get by status
    @GetMapping("/status/{status}")
    public List<WorkStudy> getByStatus(@PathVariable String status) {
        return workStudyRepository.findByStatus(status.toUpperCase());
    }

    // Create new work-study assignment
    @PostMapping
    public ResponseEntity<WorkStudy> create(@RequestBody Map<String, Object> req) {
        Long studentId = Long.valueOf(req.get("studentId").toString());
        Long jobId = Long.valueOf(req.get("jobId").toString());
        String semester = (String) req.get("semester");

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

        WorkStudy ws = new WorkStudy();
        ws.setStudent(student);
        ws.setJob(job);
        ws.setSemester(semester != null ? semester : "Spring 2026");
        ws.setStartDate(LocalDate.now());
        ws.setStatus("ACTIVE");

        if (req.get("totalHoursAllowed") != null) {
            ws.setTotalHoursAllowed(Double.valueOf(req.get("totalHoursAllowed").toString()));
        }
        if (req.get("notes") != null) {
            ws.setNotes((String) req.get("notes"));
        }

        return ResponseEntity.ok(workStudyRepository.save(ws));
    }

    // Update status (ACTIVE → COMPLETED / TERMINATED)
    @PutMapping("/{id}/status")
    public ResponseEntity<WorkStudy> updateStatus(@PathVariable Long id, @RequestParam String status) {
        WorkStudy ws = workStudyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Work-study record not found"));

        ws.setStatus(status.toUpperCase());
        if ("COMPLETED".equals(status.toUpperCase()) || "TERMINATED".equals(status.toUpperCase())) {
            ws.setEndDate(LocalDate.now());
        }

        return ResponseEntity.ok(workStudyRepository.save(ws));
    }

    // Update earnings
    @PutMapping("/{id}/earnings")
    public ResponseEntity<WorkStudy> updateEarnings(@PathVariable Long id, @RequestParam Double amount) {
        WorkStudy ws = workStudyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Work-study record not found"));

        ws.setTotalEarnings(ws.getTotalEarnings() + amount);
        return ResponseEntity.ok(workStudyRepository.save(ws));
    }

    // Delete
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        workStudyRepository.deleteById(id);
    }

    // Delete all by student (account deletion)
    @DeleteMapping("/student/{studentId}")
    @Transactional
    public void deleteByStudent(@PathVariable Long studentId) {
        workStudyRepository.deleteByStudentId(studentId);
    }
}
