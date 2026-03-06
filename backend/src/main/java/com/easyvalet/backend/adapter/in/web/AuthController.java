package com.easyvalet.backend.adapter.in.web;

import com.easyvalet.backend.application.port.in.AuthUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/signup")
    public ResponseEntity<AuthDto.SignupResponse> signup(@RequestBody AuthDto.SignupRequest request) {
        return ResponseEntity.ok(authUseCase.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDto.LoginResponse> login(@RequestBody AuthDto.LoginRequest request) {
        return ResponseEntity.ok(authUseCase.login(request));
    }
}
