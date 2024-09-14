package pat.mat.chat.app.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.*;

@Entity
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "date_created")
    private Instant dateCreated;


    @ManyToMany
    @JoinTable(
            name = "sessionusers",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "user_name")
    )
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "session", cascade = CascadeType.REMOVE)
    private List<Message> messages = new ArrayList<>();

    public Session() {
        dateCreated = Instant.now();
    }

    public Session(User sessionCreator) {
        this();
        users.add(sessionCreator);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void addUser(User user) { users.add(user); }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Session session = (Session) o;

        return Objects.equals(id, session.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", dateCreated=" + dateCreated +
                ", users=" + users +
                '}';
    }

}
