package com.aceproject.projectmanagementsystem.backend.user;

import com.aceproject.projectmanagementsystem.backend.dto.UserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class UserExtractorService {

    public
    UserDTO extractUser(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken) {
            return extractFromJwt((JwtAuthenticationToken) authentication);
        } else if (authentication instanceof OAuth2AuthenticationToken) {
            return extractFromOAuth2((OAuth2AuthenticationToken) authentication);
        }
        throw new RuntimeException("Unsupported authentication type");
    }

    private UserDTO extractFromJwt(JwtAuthenticationToken jwtToken) {
        Jwt jwt = jwtToken.getToken();
        return UserDTO.builder()
                .email(jwt.getClaimAsString("email"))
                .name(jwt.getClaimAsString("name"))
                .avatarUrl(jwt.getClaimAsString("picture"))
                .build();
    }

    private UserDTO extractFromOAuth2(OAuth2AuthenticationToken oauth2Token) {
        OAuth2User user = oauth2Token.getPrincipal();
        return UserDTO.builder()
                .email(user.getAttribute("email"))
                .name(user.getAttribute("name"))
                .avatarUrl(user.getAttribute("picture"))
                .build();
    }
}