package pat.mat.chat.app.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pat.mat.chat.app.model.Message;

import java.time.Instant;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.session.id = :sessionId ORDER BY m.timestamp ASC")
    List<Message> findLastMessagesBySessionId(@Param("sessionId") Long sessionId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.session.id = :sessionId AND m.timestamp < :timestamp ORDER BY m.timestamp ASC")
    List<Message> findLastMessagesBeforeTimestampBySessionId(
            @Param("sessionId") Long sessionId,
            @Param("timestamp") Instant timestamp,
            Pageable pageable
    );

    @Query("SELECT m FROM Message m WHERE m.session.id = :sessionId AND m.timestamp > :timestamp ORDER BY m.timestamp ASC")
    List<Message> findAllMessagesNewerThanTimestampBySessionId(@Param("sessionId") Long sessionId, @Param("timestamp") Instant timestamp);
}