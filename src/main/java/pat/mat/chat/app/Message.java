package pat.mat.chat.app;


import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "messages", indexes = {
        @Index(name = "idx_timestamp", columnList = "timestamp DESC")})
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "user_name")
    private User user;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private Session session;


    @Column(name = "timestamp")
    private Instant timestamp;


    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    public Message() {
        timestamp = Instant.now();
    }

    public Message(User sender, Session session, String content) {
        this();
        user = sender;
        this.session = session;
        this.content = content;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Instant getTimeStamp() {
        return timestamp;
    }

    public void setTimeStamp(Instant timeStamp) {
        this.timestamp = timeStamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
