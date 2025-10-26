package com.aceproject.projectmanagementsystem.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder; // Spring Security OAuth2 provides this

    @Autowired
    public JwtChannelInterceptor(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            try {
                // Extract token
                String token = extractToken(accessor);

                if (token == null) {
                    throw new IllegalArgumentException("No JWT token found");
                }

                Jwt jwt = jwtDecoder.decode(token);

                // Extract email from JWT
                String userEmail = jwt.getClaimAsString("email");

                if (userEmail == null || userEmail.isEmpty()) {
                    throw new IllegalArgumentException("No email claim in JWT token");
                }

                // Create authentication
                Collection<GrantedAuthority> authorities =
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userEmail, null, authorities);

                accessor.setUser(authentication);

            }
            catch (Exception e) {
                throw new IllegalArgumentException("Authentication failed: " + e.getMessage());
            }
        }

        return message;
    }

    private String extractToken(StompHeaderAccessor accessor) {
        // Method 1: Try to get from Authorization header in STOMP frame
        List<String> authHeaders = accessor.getNativeHeader("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String bearerToken = authHeaders.get(0);
            if (bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7);
            }
            return bearerToken;
        }
        return null;
    }
}