package com.workstudy.backend.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.workstudy.backend.model.SavedJob;
import com.workstudy.backend.model.Student;
import com.workstudy.backend.model.Job;
import com.workstudy.backend.repository.SavedJobRepository;
import com.workstudy.backend.repository.StudentRepository;
import com.workstudy.backend.repository.JobRepository;

@RestController
@RequestMapping("/api/saved-jobs")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@SuppressWarnings("null")
public class SavedJobController {

    @Autowired
    private SavedJobRepository savedJobRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JobRepository jobRepository;

    @PostMapping
    public SavedJob saveJob(@RequestParam Long studentId, @RequestParam Long jobId) {
        if (savedJobRepository.existsByStudentIdAndJobId(studentId, jobId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Job already saved");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

        SavedJob saved = new SavedJob();
        saved.setStudent(student);
        saved.setJob(job);
        return savedJobRepository.save(saved);
    }

    @GetMapping("/student/{studentId}")
    public List<SavedJob> getByStudent(@PathVariable Long studentId) {
        return savedJobRepository.findByStudentId(studentId);
    }

    @DeleteMapping
    public ResponseEntity<?> unsaveJob(@RequestParam Long studentId, @RequestParam Long jobId) {
        savedJobRepository.deleteByStudentIdAndJobId(studentId, jobId);
        Map<String, Object> res = new HashMap<>();
        res.put("message", "Job unsaved successfully");
        return ResponseEntity.ok(res);
    }
}
