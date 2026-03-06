package com.easyvalet.backend.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CarJpaRepository extends JpaRepository<CarEntity, Long> {
    Optional<CarEntity> findByCarNumber(String carNumber);
}
