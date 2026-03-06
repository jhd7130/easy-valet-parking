package com.easyvalet.backend.application.service;

import com.easyvalet.backend.application.port.in.UserUseCase;
import com.easyvalet.backend.application.port.out.UserRepositoryPort;
import com.easyvalet.backend.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
