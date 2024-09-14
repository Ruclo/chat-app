package pat.mat.chat.app.websocket;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import pat.mat.chat.app.service.JwtService;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class WebSocketSessionRegistry {

    private final ConcurrentHashMap<Jwt, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void registerSession(Jwt jwt, WebSocketSession webSocketSession) {
        sessions.put(jwt, webSocketSession);
    }

    public void removeSession(WebSocketSession session) {
        sessions.entrySet().removeIf(entry -> entry.getValue().equals(session));
    }

    public void updateSession(Jwt oldJwt, Jwt newJwt) {
        WebSocketSession session = sessions.remove(oldJwt);
        if (session != null) {
            sessions.put(newJwt, session);
        }

    }

    @Scheduled(fixedRate = JwtService.ACCESS_TOKEN_DURATION_IN_MINUTES, timeUnit = TimeUnit.MINUTES)
    public void checkAndHandleExpiredSessions() {
        System.out.println("Purging sessions");
        Instant now = Instant.now();
        sessions.forEach((jwt, session) -> {
            Instant expiration = jwt.getExpiresAt();
            if (now.isAfter(expiration)) {
                System.out.println("Expired session: " + session.toString());
                handleExpiredSession(session);
            }
        });
        System.out.println("Purge done");
    }

    private void handleExpiredSession(WebSocketSession session) {
        try {
            session.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            removeSession(session);
        }
    }
}
