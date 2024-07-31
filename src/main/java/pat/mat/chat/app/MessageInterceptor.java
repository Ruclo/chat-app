package pat.mat.chat.app;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageInterceptor implements ChannelInterceptor {

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private TopicExchange topicExchange;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        String destination = accessor.getDestination();

        if (destination != null && destination.startsWith("/exchange/")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {



            if (!destination.equals("/queue/" + accessor.getUser().getName())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }

            accessor.setNativeHeader("durable", "true");
            accessor.setNativeHeader("auto-delete", "true");
            accessor.setNativeHeader("exclusive", "false");

        }

        return message;
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception e) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            setupQueueBinding(accessor.getUser().getName());
        }

    }

    private void setupQueueBinding(String queueName) {

        List<String> sessionIds = sessionService.getUsersSessions(queueName).stream()
                .map(session -> Long.toString(session.getId())).collect(Collectors.toList());

        for (String sessionId : sessionIds) {
            Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, "chat.exchange", sessionId, null);

            rabbitAdmin.declareBinding(binding);
        }

    }
}
