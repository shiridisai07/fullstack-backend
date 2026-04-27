package com.workstudy.backend.controller;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.workstudy.backend.model.SupportTicket;
import com.workstudy.backend.model.Student;
import com.workstudy.backend.repository.SupportTicketRepository;
import com.workstudy.backend.repository.StudentRepository;

@RestController
@RequestMapping("/api/support")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@SuppressWarnings("null")
public class SupportTicketController {

    @Autowired
    private SupportTicketRepository ticketRepository;

    @Autowired
    private StudentRepository studentRepository;

    // ── Student: Create ticket ──
    @PostMapping
    public SupportTicket createTicket(@RequestBody Map<String, String> req) {
        Long studentId = Long.parseLong(req.get("studentId"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        SupportTicket ticket = new SupportTicket();
        ticket.setStudent(student);
        ticket.setSubject(req.get("subject"));
        ticket.setDescription(req.get("description"));
        if (req.get("category") != null) ticket.setCategory(req.get("category").toUpperCase());
        if (req.get("priority") != null) ticket.setPriority(req.get("priority").toUpperCase());

        return ticketRepository.save(ticket);
    }

    // ── Student: Get own tickets ──
    @GetMapping("/student/{studentId}")
    public List<SupportTicket> getMyTickets(@PathVariable Long studentId) {
        return ticketRepository.findByStudentId(studentId);
    }

    // ── Admin: Get all tickets ──
    @GetMapping
    public List<SupportTicket> getAllTickets(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String category) {

        List<SupportTicket> tickets = ticketRepository.findAll();

        if (status != null && !status.isBlank())
            tickets = tickets.stream().filter(t -> status.equalsIgnoreCase(t.getStatus())).collect(Collectors.toList());
        if (priority != null && !priority.isBlank())
            tickets = tickets.stream().filter(t -> priority.equalsIgnoreCase(t.getPriority())).collect(Collectors.toList());
        if (category != null && !category.isBlank())
            tickets = tickets.stream().filter(t -> category.equalsIgnoreCase(t.getCategory())).collect(Collectors.toList());

        tickets.sort(Comparator.comparing(SupportTicket::getCreatedAt).reversed());
        return tickets;
    }

    // ── Admin: Get single ticket ──
    @GetMapping("/{id}")
    public SupportTicket getTicket(@PathVariable Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
    }

    // ── Admin: Update status ──
    @PutMapping("/{id}/status")
    public SupportTicket updateStatus(@PathVariable Long id, @RequestParam String status) {
        List<String> valid = List.of("OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED");
        if (!valid.contains(status.toUpperCase()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");

        SupportTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        ticket.setStatus(status.toUpperCase());
        return ticketRepository.save(ticket);
    }

    // ── Admin: Update priority ──
    @PutMapping("/{id}/priority")
    public SupportTicket updatePriority(@PathVariable Long id, @RequestParam String priority) {
        SupportTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        ticket.setPriority(priority.toUpperCase());
        return ticketRepository.save(ticket);
    }

    // ── Admin: Assign ticket ──
    @PutMapping("/{id}/assign")
    public SupportTicket assignTicket(@PathVariable Long id, @RequestBody Map<String, String> req) {
        SupportTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        ticket.setAssignedAdmin(req.get("assignedAdmin"));
        if ("OPEN".equals(ticket.getStatus())) ticket.setStatus("IN_PROGRESS");
        return ticketRepository.save(ticket);
    }

    // ── Admin: Reply to ticket ──
    @PutMapping("/{id}/reply")
    public SupportTicket replyToTicket(@PathVariable Long id, @RequestBody Map<String, String> req) {
        SupportTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        ticket.setAdminReply(req.get("reply"));
        if ("OPEN".equals(ticket.getStatus()) || "IN_PROGRESS".equals(ticket.getStatus()))
            ticket.setStatus("RESOLVED");
        return ticketRepository.save(ticket);
    }

    // ── Admin: Add internal note ──
    @PutMapping("/{id}/note")
    public SupportTicket addNote(@PathVariable Long id, @RequestBody Map<String, String> req) {
        SupportTicket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
        ticket.setInternalNote(req.get("note"));
        return ticketRepository.save(ticket);
    }

    // ── Admin: Delete ticket ──
    @DeleteMapping("/{id}")
    public Map<String, String> deleteTicket(@PathVariable Long id) {
        if (!ticketRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        ticketRepository.deleteById(id);
        return Map.of("message", "Ticket deleted");
    }

    // ── Admin: Ticket stats ──
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", ticketRepository.count());
        stats.put("open", ticketRepository.countByStatus("OPEN"));
        stats.put("inProgress", ticketRepository.countByStatus("IN_PROGRESS"));
        stats.put("resolved", ticketRepository.countByStatus("RESOLVED"));
        stats.put("closed", ticketRepository.countByStatus("CLOSED"));
        return stats;
    }
}
