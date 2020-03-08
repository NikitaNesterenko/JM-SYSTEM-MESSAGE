package jm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/* @EnableWebSocketMessageBroker включает обработку сообщений по WebSocket, возвращаемую брокером сообщений.
 * Метод configureMessageBroker() переопределяет поведение по умолчанию в WebSocketMessageBrokerConfigurer для настройки брокера сообщений.
 * Он вызывает enableSimpleBroker() для включения простого брокера сообщений в памяти чтобы возвращать обратно сообщения клиенту по направлениям с префиксом /topic.
 * Он также объявляет префикс /app для сообщений, привязанных к методам, аннотированными @MessageMapping.
 * Метод registerStompEndpoints() регистрирует /websocket, включая дополнительно SockJS как альтернативный вариант обмена сообщениями, когда WebSocket не доступен.*/

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");

//        config.enableSimpleBroker("/secured/user/queue/specific-user");
//        config.setApplicationDestinationPrefixes("/spring-security-mvc-socket");
//        config.setUserDestinationPrefix("/secured/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").withSockJS();
    }

}
