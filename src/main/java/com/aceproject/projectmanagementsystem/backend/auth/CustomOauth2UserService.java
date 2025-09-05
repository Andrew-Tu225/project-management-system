package com.aceproject.projectmanagementsystem.backend.auth;

import com.aceproject.projectmanagementsystem.backend.user.User;
import com.aceproject.projectmanagementsystem.backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Autowired
    public CustomOauth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerUserId = oauth2User.getAttribute("sub");
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");

        User user = userRepository
                .findByProviderAndProviderUserId(provider, providerUserId)
                .orElseGet(() ->{
                    User newUser = new User();
                    newUser.setProvider(provider);
                    newUser.setProviderUserId(providerUserId);
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setAvatarUrl(picture);
                    newUser.setCreatedAt(Instant.now());
                    return newUser;
                        }
                );

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);
        return oauth2User;
    }
}
