package pat.mat.chat.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pat.mat.chat.app.model.Session;

import java.time.Instant;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    boolean existsByIdAndUsersUsername(Long sessionId, String username);

    List<Session> findAllByUsersUsername(String username);

    @Query(value = "SELECT cg.session_id AS session_id, cg.name AS session_name, " +
            "COALESCE(m.timestamp, cg.date_created) AS latest_timestamp " +
            "FROM (SELECT session_id FROM sessionusers WHERE user_name = :userName) ucg " +
            "JOIN session cg ON ucg.session_id = cg.session_id " +
            "LEFT JOIN LATERAL (SELECT timestamp " +
            "FROM messages " +
            "WHERE session_id = cg.session_id " +
            "ORDER BY timestamp DESC " +
            "LIMIT 1) m ON true " +
            "WHERE COALESCE(m.timestamp, cg.date_created) < COALESCE(:timestamp, CURRENT_TIMESTAMP) " +
            "ORDER BY COALESCE(m.timestamp, cg.date_created) DESC, cg.session_id " +
            "LIMIT :limit",
            countQuery = "SELECT COUNT(*) FROM sessionusers WHERE user_name = :userName",
            nativeQuery = true)

    List<Object[]> findByUserSortedByNewestMessage(
            @Param("userName") String userName,
            @Param("timestamp") Instant timestamp,
            @Param("limit") int limit);
}
