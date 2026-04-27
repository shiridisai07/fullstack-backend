package com.workstudy.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.workstudy.backend.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId AND m.receiver.id = :otherId) OR (m.sender.id = :otherId AND m.receiver.id = :userId) ORDER BY m.createdAt ASC")
    List<Message> findConversation(Long userId, Long otherId);

    @Query("SELECT m FROM Message m WHERE m.receiver.id = :userId AND m.read = false")
    List<Message> findUnreadByReceiverId(Long userId);

    long countByReceiverIdAndReadFalse(Long receiverId);

    @Query("SELECT DISTINCT CASE WHEN m.sender.id = :userId THEN m.receiver.id ELSE m.sender.id END FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    List<Long> findConversationPartnerIds(Long userId);
}
