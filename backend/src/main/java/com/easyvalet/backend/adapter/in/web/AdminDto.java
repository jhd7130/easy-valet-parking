package com.easyvalet.backend.adapter.in.web;

import com.easyvalet.backend.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class AdminDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InviteRequest {
        private String email;
        private String nickname;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StaffResponse {
        private Long id;
        private String email;
        private String nickname;
        private String role;
        private boolean active;
        private Long invitedBy;
        private LocalDateTime createdAt;

        public static StaffResponse from(User user) {
            return StaffResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .role(user.getRole().name())
                    .active(user.isActive())
                    .invitedBy(user.getInvitedBy())
                    .createdAt(user.getCreatedAt())
                    .build();
        }
    }
}
