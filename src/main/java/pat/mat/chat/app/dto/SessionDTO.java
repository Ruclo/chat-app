package pat.mat.chat.app.dto;

import pat.mat.chat.app.model.Session;

import java.time.Instant;

public class SessionDTO {

    private Long sessionId;

    private String sessionName;

    private Instant latestTimestamp;


    public SessionDTO(long sessionId, String sessionName, Instant latestTimestamp) {
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.latestTimestamp = latestTimestamp;
    }

    public SessionDTO(Session session) {
        this.sessionId = session.getId();
        this.sessionName = session.getName();
        this.latestTimestamp = session.getDateCreated();
    }
    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Instant getLatestTimestamp() {
        return latestTimestamp;
    }

    public void setLatestTimestamp(Instant latestTimestamp) {
        this.latestTimestamp = latestTimestamp;
    }
}
