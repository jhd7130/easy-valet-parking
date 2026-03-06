package com.easyvalet.backend.application.port.in;

import com.easyvalet.backend.adapter.in.web.AuthDto;

public interface AuthUseCase {
    AuthDto.LoginResponse login(AuthDto.LoginRequest request);

    AuthDto.SignupResponse signup(AuthDto.SignupRequest request);
}
