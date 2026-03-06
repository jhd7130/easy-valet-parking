package com.easyvalet.backend.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AffiliationJpaRepository extends JpaRepository<AffiliationEntity, Long> {
    List<AffiliationEntity> findByIsPaidTrue();
}
