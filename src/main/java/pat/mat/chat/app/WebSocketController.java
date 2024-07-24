package pat.mat.chat.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private MessageService messageService;

    @MessageMapping("/chat/{sessionId}")
    @PreAuthorize("@sessionService.isUserInSession(authentication.getName(), #sessionId)")
    public void handleMessage(@DestinationVariable Long sessionId,
                              @Payload String message,
                              SimpMessageHeaderAccessor headerAccessor,
                              Authentication authentication) {

        messageService.saveMessage(message, authentication.getName(), sessionId);

        messagingTemplate.convertAndSend("/pub/chat/" + sessionId, message);
    }
}
