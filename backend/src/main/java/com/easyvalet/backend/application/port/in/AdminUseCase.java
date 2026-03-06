package com.easyvalet.backend.application.port.in;

import com.easyvalet.backend.domain.User;
import java.util.List;

public interface AdminUseCase {
    List<User> getStaffByAffiliation(String adminEmail);

    User inviteStaff(String adminEmail, String email, String nickname);

    User deactivateStaff(String adminEmail, Long staffId);

    User activateStaff(String adminEmail, Long staffId);
}
