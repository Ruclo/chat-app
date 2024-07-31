package pat.mat.chat.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompConversionException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.util.HashMap;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private SessionService sessionService;

    @MessageMapping("/session/{sessionId}")
    @SendTo("/exchange/" + RabbitMQConfig.EXCHANGE_NAME + "/{sessionId}")
    public Message<?> handleMessage(@DestinationVariable Long sessionId,
                                         @AuthenticationPrincipal Principal principal,
                                         @Payload String message) {

        if (!sessionService.isUserInSession(principal.getName(), sessionId)) {
            throw new StompConversionException("invalid session");
        }
        pat.mat.chat.app.Message messageEntity = messageService.saveMessage(message, principal.getName(), sessionId);

        return MessageBuilder.withPayload(message)
                .setHeader("sender", principal.getName())
                .setHeader("servertime", messageEntity.getTimeStamp().toString())
                .setHeader("groupId", sessionId)
                .build();
    }
}
