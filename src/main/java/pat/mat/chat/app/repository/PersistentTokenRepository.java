package pat.mat.chat.app.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pat.mat.chat.app.model.PersistentToken;

import java.time.Instant;

@Repository
public interface PersistentTokenRepository extends CrudRepository<PersistentToken, String> {

    void deleteByUserUsername(String username);

    @Modifying
    @Query("DELETE FROM PersistentToken t WHERE t.expiration < :now")
    void deleteExpiredTokens(Instant now);
}
