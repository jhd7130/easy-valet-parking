package com.easyvalet.backend.adapter.out.persistence;

import com.easyvalet.backend.application.port.out.AffiliationRepositoryPort;
import com.easyvalet.backend.domain.Affiliation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AffiliationPersistenceAdapter implements AffiliationRepositoryPort {

    private final AffiliationJpaRepository affiliationJpaRepository;

    @Override
    public List<Affiliation> findAll() {
        return affiliationJpaRepository.findAll().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Affiliation> findByIsPaidTrue() {
        return affiliationJpaRepository.findByIsPaidTrue().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Affiliation> findById(Long id) {
        return affiliationJpaRepository.findById(id).map(this::mapToDomain);
    }

    @Override
    public Affiliation save(Affiliation affiliation) {
        AffiliationEntity entity = mapToEntity(affiliation);
        return mapToDomain(affiliationJpaRepository.save(entity));
    }

    private Affiliation mapToDomain(AffiliationEntity entity) {
        return Affiliation.builder()
                .id(entity.getId())
                .name(entity.getName())
                .isPaid(entity.isPaid())
                .build();
    }

    private AffiliationEntity mapToEntity(Affiliation domain) {
        return AffiliationEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .isPaid(domain.isPaid())
                .build();
    }
}
