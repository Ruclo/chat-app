package pat.mat.chat.app;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean isUserInSession(String username, Long sessionId) {
        return sessionRepository.existsByIdAndUsersUsername(sessionId, username);
    }

    @Transactional
    public Session createNewSession(User user, String otherUsername) {
        Session session = new Session(user);

        User participantUser = userRepository.findByUsername(otherUsername).orElseThrow();

        session.addUser(participantUser);

        return sessionRepository.save(session);

    }

    @Transactional
    public void addUserToSession(long sessionId, String username) {
        Session session = sessionRepository.findById(sessionId).orElseThrow();
        User user = userRepository.findByUsername(username).orElseThrow();

        session.addUser(user);
        sessionRepository.save(session);
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
}
