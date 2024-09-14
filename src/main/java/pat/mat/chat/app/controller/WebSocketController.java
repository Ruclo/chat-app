package pat.mat.chat.app.controller;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.stomp.StompConversionException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import pat.mat.chat.app.dto.MessageDTO;
import pat.mat.chat.app.service.MessageService;
import pat.mat.chat.app.service.SessionService;
import pat.mat.chat.app.config.RabbitMQConfig;

import java.security.Principal;

@Controller
public class WebSocketController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SessionService sessionService;

    @Transactional
    @MessageMapping("/session/{sessionId}")
    @SendTo("/exchange/" + RabbitMQConfig.EXCHANGE_NAME + "/{sessionId}")
    public Message<?> handleMessage(@DestinationVariable Long sessionId,
                                         @AuthenticationPrincipal Principal principal,
                                         @Payload String message) {

        if (!sessionService.isUserInSession(principal.getName(), sessionId)) {
            throw new StompConversionException("invalid session");
        }

        pat.mat.chat.app.model.Message messageEntity = messageService.saveMessage(message, principal.getName(), sessionId);
        MessageDTO messageDTO = new MessageDTO(messageEntity);

        return MessageBuilder.withPayload(messageDTO)
                .setHeader("type", "chat")
                .build();
    }
}
