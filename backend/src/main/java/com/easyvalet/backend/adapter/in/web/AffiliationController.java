package com.easyvalet.backend.adapter.in.web;

import com.easyvalet.backend.application.port.in.GetAffiliationsUseCase;
import com.easyvalet.backend.domain.Affiliation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/affiliations")
@RequiredArgsConstructor
public class AffiliationController {

    private final GetAffiliationsUseCase getAffiliationsUseCase;

    @GetMapping
    public List<Affiliation> getPaidAffiliations() {
        return getAffiliationsUseCase.getPaidAffiliations();
    }
}
