package com.easyvalet.backend.adapter.in.web;

import com.easyvalet.backend.application.port.in.AdminUseCase;
import com.easyvalet.backend.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminUseCase adminUseCase;

    @GetMapping("/staff")
    public ResponseEntity<List<AdminDto.StaffResponse>> getStaff(Authentication authentication) {
        List<AdminDto.StaffResponse> staff = adminUseCase.getStaffByAffiliation(authentication.getName())
                .stream()
                .map(AdminDto.StaffResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(staff);
    }

    @PostMapping("/staff/invite")
    public ResponseEntity<AdminDto.StaffResponse> inviteStaff(
            @RequestBody AdminDto.InviteRequest request,
            Authentication authentication) {
        User staff = adminUseCase.inviteStaff(authentication.getName(), request.getEmail(), request.getNickname());
        return ResponseEntity.ok(AdminDto.StaffResponse.from(staff));
    }

    @PostMapping("/staff/{id}/deactivate")
    public ResponseEntity<AdminDto.StaffResponse> deactivateStaff(
            @PathVariable Long id,
            Authentication authentication) {
        User staff = adminUseCase.deactivateStaff(authentication.getName(), id);
        return ResponseEntity.ok(AdminDto.StaffResponse.from(staff));
    }

    @PostMapping("/staff/{id}/activate")
    public ResponseEntity<AdminDto.StaffResponse> activateStaff(
            @PathVariable Long id,
            Authentication authentication) {
        User staff = adminUseCase.activateStaff(authentication.getName(), id);
        return ResponseEntity.ok(AdminDto.StaffResponse.from(staff));
    }
}
