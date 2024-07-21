package pat.mat.chat.app;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.session.id = :sessionId ORDER BY m.timestamp DESC")
    List<Message> findLastMessagesBySessionId(@Param("sessionId") Long sessionId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.session.id = :sessionId AND m.timestamp < :timestamp ORDER BY m.timestamp DESC")
    List<Message> findLastMessagesBeforeTimestampBySessionId(
            @Param("sessionId") Long sessionId,
            @Param("timestamp") LocalDateTime timestamp,
            Pageable pageable
    );
}