package com.workstudy.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student sender;

    @ManyToOne
    private Student receiver;

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean read;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getSender() { return sender; }
    public void setSender(Student sender) { this.sender = sender; }

    public Student getReceiver() { return receiver; }
    public void setReceiver(Student receiver) { this.receiver = receiver; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
