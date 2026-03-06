package com.easyvalet.backend.application.service;

import com.easyvalet.backend.adapter.in.web.AuthDto;
import com.easyvalet.backend.application.port.in.AuthUseCase;
import com.easyvalet.backend.application.port.out.UserRepositoryPort;
import com.easyvalet.backend.config.JwtTokenProvider;
import com.easyvalet.backend.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthDto.SignupResponse signup(AuthDto.SignupRequest request) {
        if (userRepositoryPort.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role(User.Role.USER)
                .affiliationId(request.getAffiliationId())
                .isActive(false)
                .build();

        User savedUser = userRepositoryPort.save(user);

        return AuthDto.SignupResponse.builder()
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .message("가입이 완료되었습니다. 관리자 승인 후 로그인할 수 있습니다.")
                .build();
    }

    @Override
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String email = authentication.getName();
        java.util.List<String> roles = authentication.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toList());

        String jwt = tokenProvider.createToken(email, roles);
        User user = userRepositoryPort.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return AuthDto.LoginResponse.builder()
                .accessToken(jwt)
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .affiliationId(user.getAffiliationId())
                .build();
    }
}
