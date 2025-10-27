package com.aceproject.projectmanagementsystem.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOidcUserService customOidcUserService) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().authenticated())
                .oauth2Login(oauth ->
                        oauth.userInfoEndpoint(userInfo ->
                                userInfo.oidcUserService(customOidcUserService)))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .opaqueToken(opaque -> opaque
                                .introspector(googleOpaqueTokenIntrospector()))); //authenticate opaque token from google

        return http.build();
    }

    @Bean
    public OpaqueTokenIntrospector googleOpaqueTokenIntrospector() {
        return token -> {
            WebClient webClient = WebClient.builder().build();

            try {
                Map<String, Object> userInfo = webClient
                        .get()
                        .uri("https://openidconnect.googleapis.com/v1/userinfo")
                        .headers(headers -> headers.setBearerAuth(token)) //google authenticate token
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                        })
                        .block();

                if (userInfo == null || !userInfo.containsKey("email")) {
                    throw new BadOpaqueTokenException("Invalid token");
                }

                return new OAuth2IntrospectionAuthenticatedPrincipal(
                        (String) userInfo.get("email"),
                        userInfo,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
            } catch (Exception e) {
                throw new BadOpaqueTokenException("Token validation failed: " + e.getMessage());
            }
        };
    }
}