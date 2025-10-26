package com.aceproject.projectmanagementsystem.config;

import com.aceproject.projectmanagementsystem.auth.JwtChannelInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtChannelInterceptor jwtChannelInterceptor;

    @Autowired
    public WebSocketConfig(JwtChannelInterceptor jwtChannelInterceptor) {
        this.jwtChannelInterceptor = jwtChannelInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple broker for user-specific destinations as well as general topics
        config.enableSimpleBroker("/topic", "/queue");
        config.setUserDestinationPrefix("/user"); // required for user-specific messaging
        config.setApplicationDestinationPrefixes("/app");
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-notify")
                .setAllowedOriginPatterns("http://localhost:*")
                .addInterceptors(new QueryParamHandshakeInterceptor())
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Register JWT interceptor to validate tokens on CONNECT
        registration.interceptors(jwtChannelInterceptor);
    }

    // Handshake interceptor to extract token from query parameters
    private static class QueryParamHandshakeInterceptor implements HandshakeInterceptor {

        @Override
        public boolean beforeHandshake(ServerHttpRequest request,
                                       ServerHttpResponse response,
                                       WebSocketHandler wsHandler,
                                       Map<String, Object> attributes) {

            if (request instanceof ServletServerHttpRequest) {
                // type cast request to servletServerHttpRequest to access query parameter
                ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                String token = servletRequest.getServletRequest().getParameter("token");

                if (token != null) {
                    // Store token in session attributes so it can be accessed in the interceptor
                    attributes.put("token", token);
                    System.out.println("Token extracted from query parameter during handshake");
                }
            }

            return true;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Exception exception) {
            // Nothing to do after handshake
        }
    }
}