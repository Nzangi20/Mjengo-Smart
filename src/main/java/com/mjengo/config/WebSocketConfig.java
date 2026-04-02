package com.mjengo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import org.springframework.messaging.simp.config.ChannelRegistration;

/**
 * STOMP broker for real-time chat. Clients subscribe to {@code /topic/project.{id}}
 * for project channels or {@code /topic/dm.{base64}} for direct threads.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompSubscriptionAuthorizationInterceptor subscriptionAuthorizationInterceptor;

    public WebSocketConfig(
            StompSubscriptionAuthorizationInterceptor subscriptionAuthorizationInterceptor) {
        this.subscriptionAuthorizationInterceptor = subscriptionAuthorizationInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(subscriptionAuthorizationInterceptor);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
