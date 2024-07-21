package pat.mat.chat.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public List<Message> getLatestMessagesForSession(Long sessionId, int amount) {
        return messageRepository.findLastMessagesBySessionId(sessionId, PageRequest.of(0, amount));
    }

    public List<Message> getMessagesBeforeTimestampForSession(Long sessionId, LocalDateTime timestamp, int amount) {
        return messageRepository.findLastMessagesBeforeTimestampBySessionId(sessionId, timestamp, PageRequest.of(0, amount));
    }
}