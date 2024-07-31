package pat.mat.chat.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired SessionRepository sessionRepository;

    public Message saveMessage(String message, String username, long sessionId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Session session = sessionRepository.findById(sessionId).orElseThrow();
        Message messageEntity = new Message(user, session, message);
        return messageRepository.save(messageEntity);
    }

    public List<Message> getLatestMessagesForSession(Long sessionId, int amount) {
        return messageRepository.findLastMessagesBySessionId(sessionId, PageRequest.of(0, amount));
    }

    public List<Message> getMessagesBeforeTimestampForSession(Long sessionId, Instant timestamp, int amount) {
        return messageRepository.findLastMessagesBeforeTimestampBySessionId(sessionId, timestamp, PageRequest.of(0, amount));
    }
}