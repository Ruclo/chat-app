package pat.mat.chat.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    boolean existsByIdAndUsersUsername(Long sessionId, String username);
    List<Session> findAllByUsersUsername(String username);
}
