package com.easyvalet.backend.application.port.in;

import com.easyvalet.backend.domain.User;

public interface UserUseCase {
    User getUserById(Long id);

    User getUserByEmail(String email);
}
