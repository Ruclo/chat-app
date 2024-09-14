package pat.mat.chat.app.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "users", indexes = {
        @Index(name="username_idx", columnList = "username")
})
public class User implements UserDetails {

    public static final String DEFAULT_PFP_URL = "https://res.cloudinary.com/duyfnulch/image/upload/v1723739883/pfps/defaultpfp.jpg";

    @Id
    @Column(nullable = false, unique = true)
    @NotBlank
    @Size(min=3, max=16)
    private String username;


    @Column(nullable = false)
    @NotBlank
    private String passwordHash;

    @ManyToMany(mappedBy = "users")
    private Set<Session> sessions;

    @Column(nullable = false)
    @ColumnDefault("'" + DEFAULT_PFP_URL + "'")
    private String pfpUrl = DEFAULT_PFP_URL;

    public User() {

    }

    public User(String name, String passwordHash) {
        username = name;
        this.passwordHash = passwordHash;

    }


    public String getUsername() {
        return username;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>();
    }

    public String getPassword() {
        return passwordHash;
    }

    public String getPfpUrl() {
        return pfpUrl;
    }

    public void setPfpUrl(String pfpUrl) {
        this.pfpUrl = pfpUrl;
    }

    public void addSession(Session session) {
        sessions.add(session);
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