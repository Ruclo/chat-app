package pat.mat.chat.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import pat.mat.chat.app.service.JwtService;

import java.time.Instant;

@Entity
@Table(name="tokens")
public class PersistentToken {

    @Id
    @Size(min = JwtService.SUCCESSIONID_LENGTH, max = JwtService.SUCCESSIONID_LENGTH)
    @Column(unique = true)
    private String successionID;

    @Column(nullable = false, length = 4096)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(nullable=false)
    private Instant expiration;

    public PersistentToken() {

    }

    public PersistentToken(String successionID, String tokenHash, User user, Instant expiration) {
        this.successionID = successionID;
        this.tokenHash = tokenHash;
        this.user = user;
        this.expiration = expiration;
    }

    public @Size(min = 16, max = 16) String getSuccessionID() {
        return successionID;
    }

    public void setSuccessionID(@Size(min = 16, max = 16) String successionID) {
        this.successionID = successionID;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getExpiration() {
        return expiration;
    }

    public void setExpiration(Instant expiration) {
        this.expiration = expiration;
    }
}
