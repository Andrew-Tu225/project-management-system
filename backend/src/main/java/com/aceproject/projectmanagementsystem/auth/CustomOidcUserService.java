package com.aceproject.projectmanagementsystem.auth;

import com.aceproject.projectmanagementsystem.user.User;
import com.aceproject.projectmanagementsystem.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CustomOidcUserService extends OidcUserService {
    private final UserRepository userRepository;

    @Autowired
    public CustomOidcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerUserId = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();
        String picture = oidcUser.getPicture();

        User user = userRepository
                .findByProviderAndProviderUserId(provider, providerUserId)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setProvider(provider);
                    newUser.setProviderUserId(providerUserId);
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setAvatarUrl(picture);
                    newUser.setCreatedAt(Instant.now());
                    return newUser;
                });

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        return oidcUser;
    }
}
