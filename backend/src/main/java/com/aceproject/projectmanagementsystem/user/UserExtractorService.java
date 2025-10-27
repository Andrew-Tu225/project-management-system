package com.aceproject.projectmanagementsystem.user;

import com.aceproject.projectmanagementsystem.dto.UserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserExtractorService {

    public UserDTO extractUser(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken) {
            return extractFromOAuth2((OAuth2AuthenticationToken) authentication);
        }
        else if (authentication.getPrincipal() instanceof OAuth2AuthenticatedPrincipal){
            return extractFromOAuth2AuthenticatedPrinciple((OAuth2AuthenticatedPrincipal) authentication.getPrincipal());
        }
        throw new RuntimeException("Unsupported authentication type");
    }

    private UserDTO extractFromOAuth2(OAuth2AuthenticationToken oauth2Token) {
        OAuth2User user = oauth2Token.getPrincipal();
        return UserDTO.builder()
                .email(user.getAttribute("email"))
                .name(user.getAttribute("name"))
                .avatarUrl(user.getAttribute("picture"))
                .build();
    }

    private UserDTO extractFromOAuth2AuthenticatedPrinciple(OAuth2AuthenticatedPrincipal principal) {
        Map<String, Object> attributes = principal.getAttributes();

        return UserDTO.builder()
                .email((String) attributes.get("email"))
                .name((String) attributes.get("name"))
                .avatarUrl((String) attributes.get("picture"))
                .build();
    }
}