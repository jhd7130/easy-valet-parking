package com.easyvalet.backend.adapter.out.persistence;

import com.easyvalet.backend.domain.ParkingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ParkingJpaRepository extends JpaRepository<ParkingRecordEntity, Long> {
    Optional<ParkingRecordEntity> findByTicketNumber(String ticketNumber);

    Optional<ParkingRecordEntity> findByGuestToken(String guestToken);

    List<ParkingRecordEntity> findByStatusNot(ParkingRecord.Status status);

    List<ParkingRecordEntity> findByAffiliationId(Long affiliationId);

    List<ParkingRecordEntity> findByAffiliationIdAndStatusNot(Long affiliationId, ParkingRecord.Status status);
}
