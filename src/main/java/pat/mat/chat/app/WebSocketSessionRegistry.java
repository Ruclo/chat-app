package pat.mat.chat.app;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class WebSocketSessionRegistry {

    private final ConcurrentHashMap<WebSocketSession, Instant> sessions = new ConcurrentHashMap<>();

    public void registerSession(WebSocketSession session, Instant expiration) {
        sessions.put(session, expiration);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
    }

    @Scheduled(fixedRate = JwtService.ACCESS_TOKEN_DURATION_IN_MINUTES, timeUnit = TimeUnit.MINUTES)
    public void checkAndHandleExpiredSessions() {
        System.out.println("Purging sessions");
        Instant now = Instant.now();
        sessions.forEach((session, expiration) -> {
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
