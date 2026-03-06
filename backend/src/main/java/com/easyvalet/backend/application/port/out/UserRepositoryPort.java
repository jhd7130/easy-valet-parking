package com.easyvalet.backend.application.port.out;

import com.easyvalet.backend.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByEmail(String email);

    User save(User user);

    Optional<User> findById(Long id);

    List<User> findByAffiliationId(Long affiliationId);
}
