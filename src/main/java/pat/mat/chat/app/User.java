package pat.mat.chat.app;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name="username", nullable = false, unique = true)
    @NotBlank
    @Size(min=3, max=16)
    private String username;


    @Column(name="password", nullable = false)
    @NotBlank
    @Size(min=5)
    private String password;

    @ManyToMany(mappedBy = "users")
    private Set<Session> sessions;

    public User() {

    }

    public User(String name, String password) {
        username = name;
        this.password = password;

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
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
                ", passwordHash='" + password + '\'' +
                '}';
    }
}