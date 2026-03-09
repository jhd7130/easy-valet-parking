package com.easyvalet.backend.application.service;

import com.easyvalet.backend.application.port.in.GetAffiliationsUseCase;
import com.easyvalet.backend.application.port.out.AffiliationRepositoryPort;
import com.easyvalet.backend.domain.Affiliation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AffiliationService implements GetAffiliationsUseCase {

    private final AffiliationRepositoryPort affiliationRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<Affiliation> getAllAffiliations() {
        return affiliationRepositoryPort.findAll();
    }

    @Override
    @Transactional
    public Affiliation createAffiliation(String name) {
        Affiliation affiliation = Affiliation.builder()
                .name(name)
                .isPaid(false)
                .build();
        return affiliationRepositoryPort.save(affiliation);
    }
}
