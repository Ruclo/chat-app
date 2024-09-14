package pat.mat.chat.app.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pat.mat.chat.app.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT * FROM users u WHERE u.username IN (SELECT su.user_name FROM sessionusers su WHERE su.session_id = :sessionId)",
            nativeQuery = true,
    countQuery = "SELECT COUNT(*) FROM sessionusers su WHERE su.session_id =: sessionId")
    List<User> findUsersBySessionId(@Param("sessionId") long sessionId, Pageable pageable);
}
