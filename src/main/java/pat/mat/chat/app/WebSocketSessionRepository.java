package pat.mat.chat.app;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebSocketSessionRepository extends CrudRepository<WebSocketSession, String> {

}
