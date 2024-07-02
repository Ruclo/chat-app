package pat.mat.chat.app;


import jakarta.persistence.*;
import org.springframework.data.repository.Repository;

import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(nullable = false, unique = true)
    private String username;


    @Column(nullable = false)
    private String passwordHash;

    @ManyToMany(mappedBy = "users")
    private Set<Session> sessions;

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}