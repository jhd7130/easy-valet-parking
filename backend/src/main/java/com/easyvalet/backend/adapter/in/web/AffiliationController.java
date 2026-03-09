package com.easyvalet.backend.adapter.in.web;

import com.easyvalet.backend.application.port.in.GetAffiliationsUseCase;
import com.easyvalet.backend.domain.Affiliation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/affiliations")
@RequiredArgsConstructor
public class AffiliationController {

    private final GetAffiliationsUseCase getAffiliationsUseCase;

    @GetMapping
    public List<Affiliation> getAllAffiliations() {
        return getAffiliationsUseCase.getAllAffiliations();
    }

    @PostMapping
    public ResponseEntity<Affiliation> createAffiliation(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        if (name == null || name.isBlank()) {
            throw new RuntimeException("소속 이름을 입력해주세요.");
        }
        return ResponseEntity.ok(getAffiliationsUseCase.createAffiliation(name));
    }
}
