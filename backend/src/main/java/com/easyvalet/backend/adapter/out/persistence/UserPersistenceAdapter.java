package com.easyvalet.backend.adapter.out.persistence;

import com.easyvalet.backend.application.port.out.UserRepositoryPort;
import com.easyvalet.backend.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final AffiliationJpaRepository affiliationJpaRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(this::mapToDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(this::mapToDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = mapToEntity(user);
        return mapToDomain(userJpaRepository.save(entity));
    }

    @Override
    public List<User> findByAffiliationId(Long affiliationId) {
        return userJpaRepository.findByAffiliationId(affiliationId).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    private User mapToDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .nickname(entity.getNickname())
                .role(User.Role.valueOf(entity.getRole().name()))
                .affiliationId(entity.getAffiliation() != null ? entity.getAffiliation().getId() : null)
                .isActive(entity.isActive())
                .invitedBy(entity.getInvitedBy())
                .oauthProvider(entity.getOauthProvider())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private UserEntity mapToEntity(User domain) {
        AffiliationEntity affiliation = null;
        if (domain.getAffiliationId() != null) {
            affiliation = affiliationJpaRepository.findById(domain.getAffiliationId())
                    .orElse(null);
        }

        return UserEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .nickname(domain.getNickname())
                .role(UserEntity.Role.valueOf(domain.getRole().name()))
                .affiliation(affiliation)
                .isActive(domain.isActive())
                .invitedBy(domain.getInvitedBy())
                .oauthProvider(domain.getOauthProvider())
                .build();
    }
}
