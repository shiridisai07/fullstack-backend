package com.workstudy.backend.controller;

import com.workstudy.backend.model.Feedback;
import com.workstudy.backend.model.Student;
import com.workstudy.backend.model.Job;
import com.workstudy.backend.repository.FeedbackRepository;
import com.workstudy.backend.repository.StudentRepository;
import com.workstudy.backend.repository.JobRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@SuppressWarnings("null")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JobRepository jobRepository;

    // Get all feedback
    @GetMapping
    public List<Feedback> getAll() {
        return feedbackRepository.findAll();
    }

    // Get feedback by student
    @GetMapping("/student/{studentId}")
    public List<Feedback> getByStudent(@PathVariable Long studentId) {
        return feedbackRepository.findByStudentId(studentId);
    }

    // Get feedback by job
    @GetMapping("/job/{jobId}")
    public List<Feedback> getByJob(@PathVariable Long jobId) {
        return feedbackRepository.findByJobId(jobId);
    }

    // Submit new feedback
    @PostMapping
    public ResponseEntity<Feedback> submitFeedback(@RequestBody Map<String, Object> req) {
        Long studentId = Long.valueOf(req.get("studentId").toString());
        Long jobId = req.get("jobId") != null ? Long.valueOf(req.get("jobId").toString()) : null;
        int rating = Integer.parseInt(req.get("rating").toString());
        String comment = (String) req.get("comment");
        String category = req.get("category") != null ? (String) req.get("category") : "GENERAL";

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        Feedback feedback = new Feedback();
        feedback.setStudent(student);
        feedback.setRating(rating);
        feedback.setComment(comment);
        feedback.setCategory(category);

        if (jobId != null) {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
            feedback.setJob(job);
        }

        return ResponseEntity.ok(feedbackRepository.save(feedback));
    }

    // Delete feedback
    @DeleteMapping("/{id}")
    public void deleteFeedback(@PathVariable Long id) {
        feedbackRepository.deleteById(id);
    }

    // Delete all feedback by student (used in account deletion)
    @DeleteMapping("/student/{studentId}")
    @Transactional
    public void deleteByStudent(@PathVariable Long studentId) {
        feedbackRepository.deleteByStudentId(studentId);
    }
}
