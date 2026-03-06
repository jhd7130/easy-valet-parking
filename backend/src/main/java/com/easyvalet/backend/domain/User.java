package com.easyvalet.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String email;
    @JsonIgnore
    private String password;
    private String nickname;
    private Role role;
    private Long affiliationId;
    private boolean isActive;
    private Long invitedBy;
    private String oauthProvider;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Role {
        USER, ADMIN
    }
}
