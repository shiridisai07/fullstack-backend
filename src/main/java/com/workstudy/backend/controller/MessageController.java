package com.workstudy.backend.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.workstudy.backend.model.Message;
import com.workstudy.backend.model.Student;
import com.workstudy.backend.repository.MessageRepository;
import com.workstudy.backend.repository.StudentRepository;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@SuppressWarnings("null")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping
    public Message send(@RequestBody Map<String, String> req) {
        Long senderId = Long.parseLong(req.get("senderId"));
        Long receiverId = Long.parseLong(req.get("receiverId"));

        Student sender = studentRepository.findById(senderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sender not found"));
        Student receiver = studentRepository.findById(receiverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver not found"));

        Message msg = new Message();
        msg.setSender(sender);
        msg.setReceiver(receiver);
        msg.setContent(req.get("content"));
        return messageRepository.save(msg);
    }

    @GetMapping("/conversation")
    public List<Message> getConversation(@RequestParam Long userId, @RequestParam Long otherId) {
        return messageRepository.findConversation(userId, otherId);
    }

    @GetMapping("/conversations/{userId}")
    public List<Map<String, Object>> getConversationList(@PathVariable Long userId) {
        List<Long> partnerIds = messageRepository.findConversationPartnerIds(userId);
        List<Map<String, Object>> conversations = new ArrayList<>();

        for (Long partnerId : partnerIds) {
            Student partner = studentRepository.findById(partnerId).orElse(null);
            if (partner == null) continue;

            List<Message> msgs = messageRepository.findConversation(userId, partnerId);
            Message lastMsg = msgs.isEmpty() ? null : msgs.get(msgs.size() - 1);

            Map<String, Object> convo = new HashMap<>();
            convo.put("partnerId", partnerId);
            convo.put("partnerName", partner.getName());
            convo.put("partnerEmail", partner.getEmail());
            convo.put("partnerRole", partner.getRole());
            convo.put("lastMessage", lastMsg != null ? lastMsg.getContent() : "");
            convo.put("lastMessageTime", lastMsg != null ? lastMsg.getCreatedAt() : null);
            convo.put("unreadCount", messageRepository.countByReceiverIdAndReadFalse(userId));
            conversations.add(convo);
        }

        return conversations;
    }

    @GetMapping("/unread/{userId}")
    public ResponseEntity<?> getUnreadCount(@PathVariable Long userId) {
        Map<String, Object> res = new HashMap<>();
        res.put("count", messageRepository.countByReceiverIdAndReadFalse(userId));
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{id}/read")
    public Message markRead(@PathVariable Long id) {
        Message msg = messageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
        msg.setRead(true);
        return messageRepository.save(msg);
    }
}
