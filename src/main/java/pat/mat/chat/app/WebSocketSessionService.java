package pat.mat.chat.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Service
public class WebSocketSessionService {

    @Autowired
    private WebSocketSessionRepository webSocketSessionRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String wsSessionId = headerAccessor.getSessionId();
        WebSocketSession webSocketSession = new WebSocketSession(wsSessionId);
        webSocketSessionRepository.save(webSocketSession);


        //SUBSCRIBETOENDPOINTS
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        webSocketSessionRepository.deleteById(sessionId);
    }
}