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
    public List<Affiliation> getPaidAffiliations() {
        return affiliationRepositoryPort.findByIsPaidTrue();
    }
}
