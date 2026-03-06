package com.easyvalet.backend.config;

import com.easyvalet.backend.application.port.out.UserRepositoryPort;
import com.easyvalet.backend.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepositoryPort userRepositoryPort;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        Optional<User> existingUser = userRepositoryPort.findByEmail(email);
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = userRepositoryPort.save(User.builder()
                    .email(email)
                    .nickname(name)
                    .role(User.Role.USER)
                    .isActive(false)
                    .oauthProvider("google")
                    .build());
        }

        if (!user.isActive()) {
            response.sendRedirect(frontendUrl + "/login?error=pending_approval");
            return;
        }

        String token = jwtTokenProvider.createToken(user.getEmail(), List.of("ROLE_" + user.getRole().name()));
        response.sendRedirect(frontendUrl + "/oauth2/callback?token=" + token);
    }
}
