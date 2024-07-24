package pat.mat.chat.app;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
public class WebSocketSession {

    @Id
    @Column(length = 36)
    private String wsSessionId;

    @Column
    private final LocalDateTime expirationTime;

    public WebSocketSession() {
        expirationTime = LocalDateTime.now().plusMinutes(JwtService.ACCESS_TOKEN_DURATION_IN_MINUTES);
    }

    public WebSocketSession(String wsSessionId) {
        this();
        this.wsSessionId = wsSessionId;
    }

    public String getWsSessionId() {
        return wsSessionId;
    }

    public void setWsSessionId(String wsSessionId) {
        this.wsSessionId = wsSessionId;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

}
