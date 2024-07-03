package pat.mat.chat.app;


import jakarta.persistence.*;
import org.springframework.data.repository.Repository;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(nullable = false, unique = true, length = 20)
    private String username;


    @Column(nullable = false)
    private String passwordHash;

    @ManyToMany(mappedBy = "users")
    private Set<Session> sessions;

    public User() {

    }

    public User(String name, String passwordHash) {
        username = name;
        this.passwordHash = passwordHash;

    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                '}';
    }
}