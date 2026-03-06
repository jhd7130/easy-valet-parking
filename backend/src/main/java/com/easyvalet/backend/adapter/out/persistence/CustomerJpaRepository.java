package com.easyvalet.backend.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long> {
    @Query("SELECT c FROM CustomerEntity c JOIN c.cars car WHERE car.carNumber LIKE %:carNumber%")
    List<CustomerEntity> findByCarNumber(@Param("carNumber") String carNumber);

    @Query("SELECT c FROM CustomerEntity c WHERE c.phoneNumber LIKE %:phoneNumber%")
    List<CustomerEntity> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    Optional<CustomerEntity> findByNameAndPhoneNumber(String name, String phoneNumber);
}
