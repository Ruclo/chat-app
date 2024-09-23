package pat.mat.chat.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pat.mat.chat.app.dto.MessageDTO;
import pat.mat.chat.app.model.Message;
import pat.mat.chat.app.model.Session;
import pat.mat.chat.app.model.User;
import pat.mat.chat.app.repository.MessageRepository;
import pat.mat.chat.app.repository.SessionRepository;
import pat.mat.chat.app.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    SessionRepository sessionRepository;

    public Message saveMessage(String message, String username, long sessionId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Session session = sessionRepository.findById(sessionId).orElseThrow();
        Message messageEntity = new Message(user, session, message);
        return messageRepository.save(messageEntity);
    }

    public List<MessageDTO> getLatestMessagesForSession(Long sessionId, int amount) {
        return messageRepository.findLastMessagesBySessionId(sessionId,
                PageRequest.of(0, amount)).stream().map(MessageDTO::new)
                .collect(Collectors.toList()).reversed();
    }

    public List<MessageDTO> getMessagesBeforeTimestampForSession(Long sessionId, Instant timestamp, int amount) {
        return messageRepository.findLastMessagesBeforeTimestampBySessionId(sessionId,
                        timestamp,
                        PageRequest.of(0, amount)).stream().map(MessageDTO::new)
                .collect(Collectors.toList()).reversed();
    }

    public List<MessageDTO> getAllMessagesAfterTimestampForSession(Long sessionId, Instant timestamp) {
        return messageRepository.findAllMessagesNewerThanTimestampBySessionId(sessionId, timestamp).stream().map(MessageDTO::new).collect(Collectors.toList());
    }
}