package pat.mat.chat.app;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersistentTokenRepository extends CrudRepository<PersistentToken, String> {

    void deleteByUserUsername(String username);
}
