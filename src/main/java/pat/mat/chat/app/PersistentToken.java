package pat.mat.chat.app;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="tokens")
public class PersistentToken {

    @Id
    @Size(min = JwtService.SUCCESSIONID_LENGTH, max = JwtService.SUCCESSIONID_LENGTH)
    @Column(unique = true)
    private String successionID;

    @Column(nullable = false, length = 4096)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    public PersistentToken() {

    }

    public PersistentToken(String successionID, String token, User user) {
        this.successionID = successionID;
        this.token = token;
        this.user = user;
    }

    public @Size(min = 16, max = 16) String getSuccessionID() {
        return successionID;
    }

    public void setSuccessionID(@Size(min = 16, max = 16) String successionID) {
        this.successionID = successionID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
