package pat.mat.chat.app;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

public class LoggingChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        System.out.println("Destination: " + accessor.getDestination());
        System.out.println("Headers: " + message.getHeaders());
        System.out.println("Payload: " + message.getPayload());
        System.out.println("-------------------");

        return message;
    }
}
