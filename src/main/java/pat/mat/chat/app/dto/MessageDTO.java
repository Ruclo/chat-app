package pat.mat.chat.app.dto;

import pat.mat.chat.app.model.Message;

import java.time.Instant;

public class MessageDTO {

    private Long id;

    private UserDTO sender;

    private Long sessionId;

    private Instant timestamp;

    private String content;

    public MessageDTO(Long id, UserDTO sender, Long sessionId, Instant timestamp, String content) {
        this.id = id;
        this.sender = sender;
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.content = content;

    }

    public MessageDTO(Message message) {
        this.id = message.getId();
        this.sender = new UserDTO(message.getUser());
        this.sessionId = message.getSession().getId();
        this.timestamp = message.getTimeStamp();
        this.content = message.getContent();

    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDTO getSender() {
        return sender;
    }

    public void setSender(UserDTO sender) {
        this.sender = sender;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
