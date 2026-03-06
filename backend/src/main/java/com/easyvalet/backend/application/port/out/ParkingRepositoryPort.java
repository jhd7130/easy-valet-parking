package com.easyvalet.backend.application.port.out;

import com.easyvalet.backend.domain.ParkingRecord;
import java.util.List;
import java.util.Optional;

public interface ParkingRepositoryPort {
    ParkingRecord save(ParkingRecord record);

    List<ParkingRecord> findAll();

    List<ParkingRecord> findByAffiliationId(Long affiliationId);

    List<ParkingRecord> findByStatusNot(ParkingRecord.Status status);

    List<ParkingRecord> findByAffiliationIdAndStatusNot(Long affiliationId, ParkingRecord.Status status);

    Optional<ParkingRecord> findById(Long id);

    Optional<ParkingRecord> findByTicketNumber(String ticketNumber);

    Optional<ParkingRecord> findByGuestToken(String guestToken);
}
