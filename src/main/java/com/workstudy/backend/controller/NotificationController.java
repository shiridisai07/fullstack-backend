package com.workstudy.backend.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.workstudy.backend.model.Notification;
import com.workstudy.backend.model.Student;
import com.workstudy.backend.repository.NotificationRepository;
import com.workstudy.backend.repository.StudentRepository;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@SuppressWarnings("null")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping
    public Notification create(@RequestBody Map<String, String> req) {
        Long studentId = Long.parseLong(req.get("studentId"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        Notification notif = new Notification();
        notif.setStudent(student);
        notif.setType(req.getOrDefault("type", "system"));
        notif.setTitle(req.get("title"));
        notif.setMessage(req.get("message"));
        notif.setPriority(req.getOrDefault("priority", "normal"));
        return notificationRepository.save(notif);
    }

    @GetMapping("/student/{studentId}")
    public List<Notification> getByStudent(@PathVariable Long studentId) {
        return notificationRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    @GetMapping("/student/{studentId}/unread")
    public List<Notification> getUnread(@PathVariable Long studentId) {
        return notificationRepository.findByStudentIdAndReadFalseOrderByCreatedAtDesc(studentId);
    }

    @GetMapping("/student/{studentId}/count")
    public ResponseEntity<?> getUnreadCount(@PathVariable Long studentId) {
        Map<String, Object> res = new HashMap<>();
        res.put("count", notificationRepository.countByStudentIdAndReadFalse(studentId));
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{id}/read")
    public Notification markRead(@PathVariable Long id) {
        Notification notif = notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        notif.setRead(true);
        return notificationRepository.save(notif);
    }

    @PutMapping("/student/{studentId}/read-all")
    public ResponseEntity<?> markAllRead(@PathVariable Long studentId) {
        notificationRepository.markAllReadByStudentId(studentId);
        Map<String, Object> res = new HashMap<>();
        res.put("message", "All notifications marked as read");
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        notificationRepository.deleteById(id);
        Map<String, Object> res = new HashMap<>();
        res.put("message", "Notification deleted");
        return ResponseEntity.ok(res);
    }
}
