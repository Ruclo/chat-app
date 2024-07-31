package pat.mat.chat.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.security.Principal;


public class CustomWebSocketHandlerDecorator extends WebSocketHandlerDecorator {

    private final WebSocketSessionRegistry sessionRegistry;

    public CustomWebSocketHandlerDecorator(WebSocketHandler delegate, WebSocketSessionRegistry registry) {
        super(delegate);
        sessionRegistry = registry;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        sessionRegistry.registerSession(session, ((JwtAuthenticationToken) session.getPrincipal()).getToken().getExpiresAt());

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessionRegistry.removeSession(session);
        super.afterConnectionClosed(session, closeStatus);
    }

}