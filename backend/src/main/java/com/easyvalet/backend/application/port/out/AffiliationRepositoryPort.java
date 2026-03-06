package com.easyvalet.backend.application.port.out;

import com.easyvalet.backend.domain.Affiliation;
import java.util.List;
import java.util.Optional;

public interface AffiliationRepositoryPort {
    List<Affiliation> findAll();

    List<Affiliation> findByIsPaidTrue();

    Optional<Affiliation> findById(Long id);

    Affiliation save(Affiliation affiliation);
}
