package com.easyvalet.backend.adapter.in.web;

import com.easyvalet.backend.application.port.in.UserUseCase;
import com.easyvalet.backend.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class CustomUserController {

    private final UserUseCase userUseCase;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        User user = userUseCase.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(user);
    }
}
