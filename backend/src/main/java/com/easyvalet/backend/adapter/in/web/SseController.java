package com.easyvalet.backend.adapter.in.web;

import com.easyvalet.backend.application.port.in.UserUseCase;
import com.easyvalet.backend.application.service.ParkingEventPublisher;
import com.easyvalet.backend.config.JwtTokenProvider;
import com.easyvalet.backend.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final ParkingEventPublisher parkingEventPublisher;
    private final UserUseCase userUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping(value = "/parking-updates", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }
        String email = jwtTokenProvider.getUserEmail(token);
        User user = userUseCase.getUserByEmail(email);
        return parkingEventPublisher.subscribe(user.getAffiliationId(), user.getId());
    }
}
