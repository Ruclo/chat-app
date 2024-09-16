package pat.mat.chat.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import pat.mat.chat.app.websocket.CustomWebSocketHandlerDecorator;
import pat.mat.chat.app.websocket.MessageInterceptor;
import pat.mat.chat.app.websocket.WebSocketSessionRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Autowired
    private WebSocketSessionRegistry sessionRegistry;


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(messageInterceptor);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableStompBrokerRelay("/queue", "/exchange")
                .setRelayHost(System.getenv("BROKER_HOST"))
                .setRelayPort(Integer.parseInt(System.getenv("BROKER_PORT")))
                .setClientLogin("guest")
                .setClientPasscode("guest");
        config.setApplicationDestinationPrefixes("/send");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(8192);
        registration.addDecoratorFactory(handler -> new CustomWebSocketHandlerDecorator(handler, sessionRegistry));
    }
}