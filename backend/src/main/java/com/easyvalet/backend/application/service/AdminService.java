package com.easyvalet.backend.application.service;

import com.easyvalet.backend.application.port.in.AdminUseCase;
import com.easyvalet.backend.application.port.out.UserRepositoryPort;
import com.easyvalet.backend.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService implements AdminUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<User> getStaffByAffiliation(String adminEmail) {
        User admin = getAdmin(adminEmail);
        return userRepositoryPort.findByAffiliationId(admin.getAffiliationId());
    }

    @Override
    @Transactional
    public User inviteStaff(String adminEmail, String email, String nickname) {
        User admin = getAdmin(adminEmail);

        if (userRepositoryPort.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);

        User staff = User.builder()
                .email(email)
                .password(passwordEncoder.encode(tempPassword))
                .nickname(nickname)
                .role(User.Role.USER)
                .affiliationId(admin.getAffiliationId())
                .isActive(true)
                .invitedBy(admin.getId())
                .build();

        return userRepositoryPort.save(staff);
    }

    @Override
    @Transactional
    public User deactivateStaff(String adminEmail, Long staffId) {
        User admin = getAdmin(adminEmail);
        User staff = userRepositoryPort.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        validateSameAffiliation(admin, staff);

        User updated = User.builder()
                .id(staff.getId())
                .email(staff.getEmail())
                .password(staff.getPassword())
                .nickname(staff.getNickname())
                .role(staff.getRole())
                .affiliationId(staff.getAffiliationId())
                .isActive(false)
                .invitedBy(staff.getInvitedBy())
                .build();

        return userRepositoryPort.save(updated);
    }

    @Override
    @Transactional
    public User activateStaff(String adminEmail, Long staffId) {
        User admin = getAdmin(adminEmail);
        User staff = userRepositoryPort.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        validateSameAffiliation(admin, staff);

        User updated = User.builder()
                .id(staff.getId())
                .email(staff.getEmail())
                .password(staff.getPassword())
                .nickname(staff.getNickname())
                .role(staff.getRole())
                .affiliationId(staff.getAffiliationId())
                .isActive(true)
                .invitedBy(staff.getInvitedBy())
                .build();

        return userRepositoryPort.save(updated);
    }

    private User getAdmin(String email) {
        User admin = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (admin.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Only ADMIN can perform this action");
        }
        return admin;
    }

    private void validateSameAffiliation(User admin, User staff) {
        if (!admin.getAffiliationId().equals(staff.getAffiliationId())) {
            throw new RuntimeException("Cannot manage staff from different affiliation");
        }
    }
}
