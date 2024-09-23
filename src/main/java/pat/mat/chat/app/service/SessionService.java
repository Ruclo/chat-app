package pat.mat.chat.app.service;

import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.messaging.Message;
import pat.mat.chat.app.dto.MessageDTO;
import pat.mat.chat.app.dto.SessionCreationDTO;
import pat.mat.chat.app.dto.SessionDTO;
import pat.mat.chat.app.model.Session;
import pat.mat.chat.app.model.User;
import pat.mat.chat.app.repository.SessionRepository;
import pat.mat.chat.app.repository.UserRepository;

@Service
public class SessionService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageService messageService;

    public boolean isUserInSession(String username, Long sessionId) {
        return sessionRepository.existsByIdAndUsersUsername(sessionId, username);
    }

    public SessionDTO createNewSession(User user, SessionCreationDTO sessionCreationDTO) {
        Session session = new Session(user);
        session.setName(sessionCreationDTO.getName());

        return new SessionDTO(sessionRepository.save(session));

    }

    @Transactional
    public void addUserToSession(long sessionId, String username) {
        Session session = sessionRepository.findById(sessionId).orElseThrow();
        User user = userRepository.findByUsername(username).orElseThrow();

        session.addUser(user);
        sessionRepository.save(session);

        sendNewSessionNotifToUser(username, session);
    }

    @Transactional
    public void removeUser(long sessionId, User user) {
        Session session = sessionRepository.findById(sessionId).orElseThrow();
        session.getUsers().remove(user);

        if (session.getUsers().isEmpty()) {
            sessionRepository.delete(session);
        } else {
            sessionRepository.save(session);
        }

    }

    public List<Session> getUsersSessions(String username) {
        return sessionRepository.findAllByUsersUsername(username);
    }

    public List<SessionDTO> getUsersSessionsOrdered(String username) {
        List<Object[]> result = sessionRepository.findByUserSortedByNewestMessage(username);

        return result.stream()
                .map(res -> new SessionDTO(
                        ((Number) res[0]).longValue(),        // session_id
                        (String) res[1],                     // session_name
                        res[2] != null ? (Instant) res[2] : null // latest_message_timestamp
                ))
                .collect(Collectors.toList());
    }

    private void sendNewSessionNotifToUser(String username, Session session) {

        Instant timestamp = messageService.getLatestMessagesForSession(session.getId(), 1).stream().findFirst().map(MessageDTO::getTimestamp).orElse(session.getDateCreated());

        SessionDTO sessionDTO = new SessionDTO(session.getId(), session.getName(), timestamp);
        Message<?> message = MessageBuilder.withPayload(sessionDTO)
                        .setHeader("type", "session_new").build();

        rabbitTemplate.convertAndSend("", username, message);
    }

}
