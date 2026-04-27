package com.workstudy.backend.controller;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;

import com.workstudy.backend.model.*;
import com.workstudy.backend.repository.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@SuppressWarnings("null")
public class AdminController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private WorkHourRepository workHourRepository;

    @Autowired
    private SavedJobRepository savedJobRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ReferralRepository referralRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    // ===================== DASHBOARD ANALYTICS =====================

    @GetMapping("/stats")
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalStudents", studentRepository.count());
        stats.put("totalJobs", jobRepository.count());
        stats.put("totalApplications", applicationRepository.count());
        stats.put("totalHoursLogged", workHourRepository.count());
        stats.put("totalCertificates", certificateRepository.count());
        stats.put("totalReferrals", referralRepository.count());

        // Application funnel
        List<Application> allApps = applicationRepository.findAll();
        long pending = allApps.stream().filter(a -> "PENDING".equals(a.getStatus())).count();
        long approved = allApps.stream().filter(a -> "APPROVED".equals(a.getStatus())).count();
        long rejected = allApps.stream().filter(a -> "REJECTED".equals(a.getStatus())).count();
        stats.put("pendingApplications", pending);
        stats.put("approvedApplications", approved);
        stats.put("rejectedApplications", rejected);

        // Hours summary
        List<WorkHour> allHours = workHourRepository.findAll();
        double totalHrs = allHours.stream().mapToDouble(WorkHour::getHours).sum();
        long paidCount = allHours.stream().filter(h -> "PAID".equals(h.getStatus())).count();
        stats.put("totalHoursValue", totalHrs);
        stats.put("paidTimesheets", paidCount);

        // Blocked students
        long blocked = studentRepository.findAll().stream().filter(Student::isBlocked).count();
        stats.put("blockedStudents", blocked);

        return stats;
    }

    // ===================== STUDENT MANAGEMENT =====================

    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/students/{id}")
    public Student getStudent(@PathVariable Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    }

    @PutMapping("/students/{id}")
    public Student updateStudent(@PathVariable Long id, @RequestBody Student updated) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        if (updated.getName() != null) s.setName(updated.getName());
        if (updated.getEmail() != null) s.setEmail(updated.getEmail());
        if (updated.getRole() != null) s.setRole(updated.getRole());
        if (updated.getPhone() != null) s.setPhone(updated.getPhone());
        if (updated.getBio() != null) s.setBio(updated.getBio());
        if (updated.getSkills() != null) s.setSkills(updated.getSkills());
        if (updated.getGithubUrl() != null) s.setGithubUrl(updated.getGithubUrl());
        if (updated.getLinkedinUrl() != null) s.setLinkedinUrl(updated.getLinkedinUrl());
        if (updated.getPortfolioUrl() != null) s.setPortfolioUrl(updated.getPortfolioUrl());
        if (updated.getStatus() != null) s.setStatus(updated.getStatus());
        return studentRepository.save(s);
    }

    @PutMapping("/students/{id}/block")
    public Student blockStudent(@PathVariable Long id) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        s.setBlocked(true);
        return studentRepository.save(s);
    }

    @PutMapping("/students/{id}/unblock")
    public Student unblockStudent(@PathVariable Long id) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        s.setBlocked(false);
        return studentRepository.save(s);
    }

    @PutMapping("/students/{id}/reset-password")
    public Map<String, String> resetStudentPassword(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        String newPassword = payload.get("password");
        if (newPassword == null || newPassword.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters");
        }
        s.setPassword(org.springframework.security.crypto.bcrypt.BCrypt.hashpw(newPassword, org.springframework.security.crypto.bcrypt.BCrypt.gensalt()));
        studentRepository.save(s);
        return Map.of("message", "Password reset successfully");
    }

    @Transactional
    @DeleteMapping("/students/{id}")
    public Map<String, String> deleteStudent(@PathVariable Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
        savedJobRepository.deleteByStudentId(id);
        notificationRepository.deleteByStudentId(id);
        certificateRepository.deleteByStudentId(id);
        referralRepository.deleteByReferrerId(id);
        applicationRepository.deleteByStudentId(id);
        workHourRepository.deleteByStudentId(id);
        studentRepository.deleteById(id);
        return Map.of("message", "Student deleted successfully");
    }

    @GetMapping("/students/{id}/activity")
    public Map<String, Object> getStudentActivity(@PathVariable Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
        Map<String, Object> activity = new LinkedHashMap<>();
        activity.put("applications", applicationRepository.findByStudentId(id).size());
        activity.put("savedJobs", savedJobRepository.findByStudentId(id).size());
        activity.put("hoursLogged", workHourRepository.findByStudentId(id).size());
        activity.put("certificates", certificateRepository.findByStudentId(id).size());
        activity.put("notifications", notificationRepository.findByStudentIdOrderByCreatedAtDesc(id).size());
        return activity;
    }

    // ===================== JOB MANAGEMENT =====================

    @GetMapping("/jobs")
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @PostMapping("/jobs")
    public Job createJob(@RequestBody Job job) {
        if (job.getStatus() == null || job.getStatus().isBlank()) job.setStatus("ACTIVE");
        if (job.getHourlyRate() == null) job.setHourlyRate(15.0);
        if (job.getHoursPerWeek() <= 0) job.setHoursPerWeek(20);
        return jobRepository.save(job);
    }

    @PutMapping("/jobs/{id}")
    public Job updateJob(@PathVariable Long id, @RequestBody Job updated) {
        Job j = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        if (updated.getTitle() != null) j.setTitle(updated.getTitle());
        if (updated.getDescription() != null) j.setDescription(updated.getDescription());
        if (updated.getRequiredSkills() != null) j.setRequiredSkills(updated.getRequiredSkills());
        if (updated.getHourlyRate() != null) j.setHourlyRate(updated.getHourlyRate());
        if (updated.getHoursPerWeek() > 0) j.setHoursPerWeek(updated.getHoursPerWeek());
        if (updated.getStatus() != null) j.setStatus(updated.getStatus());
        return jobRepository.save(j);
    }

    @PutMapping("/jobs/{id}/status")
    public Job updateJobStatus(@PathVariable Long id, @RequestParam String status) {
        Job j = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        if (!List.of("ACTIVE", "PAUSED", "ARCHIVED").contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status. Must be ACTIVE, PAUSED, or ARCHIVED");
        }
        j.setStatus(status);
        return jobRepository.save(j);
    }

    @Transactional
    @DeleteMapping("/jobs/{id}")
    public Map<String, String> deleteJob(@PathVariable Long id) {
        if (!jobRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found");
        }
        savedJobRepository.deleteByJobId(id);
        workHourRepository.deleteByJobId(id);
        applicationRepository.deleteByJobId(id);
        jobRepository.deleteById(id);
        return Map.of("message", "Job deleted successfully");
    }

    // ===================== APPLICATION MANAGEMENT =====================

    @GetMapping("/applications")
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    @PutMapping("/applications/{id}/status")
    public Application updateApplicationStatus(@PathVariable Long id, @RequestParam String status) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
        app.setStatus(status);
        return applicationRepository.save(app);
    }

    @DeleteMapping("/applications/{id}")
    public Map<String, String> deleteApplication(@PathVariable Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found");
        }
        applicationRepository.deleteById(id);
        return Map.of("message", "Application deleted");
    }

    // ===================== HOURS & PAYMENTS =====================

    @GetMapping("/hours")
    public List<WorkHour> getAllHours() {
        return workHourRepository.findAll();
    }

    @PutMapping("/hours/{id}/approve")
    public WorkHour approveHours(@PathVariable Long id) {
        WorkHour wh = workHourRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "WorkHour not found"));
        wh.setStatus("APPROVED");
        return workHourRepository.save(wh);
    }

    @PutMapping("/hours/{id}/reject")
    public WorkHour rejectHours(@PathVariable Long id) {
        WorkHour wh = workHourRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "WorkHour not found"));
        wh.setStatus("REJECTED");
        return workHourRepository.save(wh);
    }

    // ===================== NOTIFICATIONS (Admin → Students) =====================

    @PostMapping("/notifications/broadcast")
    public Map<String, String> broadcastNotification(@RequestBody Map<String, String> payload) {
        String title = payload.get("title");
        String type = payload.getOrDefault("type", "system");
        String priority = payload.getOrDefault("priority", "normal");

        if (title == null || title.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }

        List<Student> students = studentRepository.findAll().stream()
                .filter(s -> "student".equals(s.getRole()) && !s.isBlocked())
                .collect(Collectors.toList());

        for (Student s : students) {
            Notification n = new Notification();
            n.setStudent(s);
            n.setTitle(title);
            n.setType(type);
            n.setPriority(priority);
            notificationRepository.save(n);
        }

        return Map.of("message", "Notification sent to " + students.size() + " students");
    }

    @PostMapping("/notifications/send")
    public Notification sendNotification(@RequestBody Notification notification) {
        return notificationRepository.save(notification);
    }

    // ===================== CERTIFICATES =====================

    @GetMapping("/certificates")
    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    @PostMapping("/certificates")
    public Certificate issueCertificate(@RequestBody Certificate cert) {
        return certificateRepository.save(cert);
    }

    @DeleteMapping("/certificates/{id}")
    public Map<String, String> deleteCertificate(@PathVariable Long id) {
        certificateRepository.deleteById(id);
        return Map.of("message", "Certificate deleted");
    }

    // ===================== REFERRALS =====================

    @GetMapping("/referrals")
    public List<Referral> getAllReferrals() {
        return referralRepository.findAll();
    }

    // ===================== SAVED JOBS =====================

    @GetMapping("/saved-jobs")
    public List<SavedJob> getAllSavedJobs() {
        return savedJobRepository.findAll();
    }

    // ===================== MESSAGES =====================

    @GetMapping("/messages")
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @DeleteMapping("/messages/{id}")
    public Map<String, String> deleteMessage(@PathVariable Long id) {
        messageRepository.deleteById(id);
        return Map.of("message", "Message deleted");
    }
}
