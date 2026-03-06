package com.easyvalet.backend.adapter.out.persistence;

import com.easyvalet.backend.application.port.out.ParkingRepositoryPort;
import com.easyvalet.backend.domain.ParkingRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ParkingPersistenceAdapter implements ParkingRepositoryPort {

    private final ParkingJpaRepository parkingJpaRepository;

    @Override
    public ParkingRecord save(ParkingRecord record) {
        ParkingRecordEntity entity = mapToEntity(record);
        return mapToDomain(parkingJpaRepository.save(entity));
    }

    @Override
    public List<ParkingRecord> findAll() {
        return parkingJpaRepository.findAll().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParkingRecord> findByStatusNot(ParkingRecord.Status status) {
        return parkingJpaRepository.findByStatusNot(status).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParkingRecord> findByAffiliationId(Long affiliationId) {
        return parkingJpaRepository.findByAffiliationId(affiliationId).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParkingRecord> findByAffiliationIdAndStatusNot(Long affiliationId, ParkingRecord.Status status) {
        return parkingJpaRepository.findByAffiliationIdAndStatusNot(affiliationId, status).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ParkingRecord> findById(Long id) {
        return parkingJpaRepository.findById(id).map(this::mapToDomain);
    }

    @Override
    public Optional<ParkingRecord> findByTicketNumber(String ticketNumber) {
        return parkingJpaRepository.findByTicketNumber(ticketNumber).map(this::mapToDomain);
    }

    @Override
    public Optional<ParkingRecord> findByGuestToken(String guestToken) {
        return parkingJpaRepository.findByGuestToken(guestToken).map(this::mapToDomain);
    }

    private ParkingRecord mapToDomain(ParkingRecordEntity entity) {
        return ParkingRecord.builder()
                .id(entity.getId())
                .ticketNumber(entity.getTicketNumber())
                .carNumber(entity.getCarNumber())
                .customerName(entity.getCustomerName())
                .phoneNumber(entity.getPhoneNumber())
                .parkingArea(entity.getParkingArea())
                .entryTime(entity.getEntryTime())
                .exitTime(entity.getExitTime())
                .status(entity.getStatus())
                .affiliationId(entity.getAffiliationId())
                .registeredBy(entity.getRegisteredBy())
                .exitRequestedBy(entity.getExitRequestedBy())
                .exitAssignedTo(entity.getExitAssignedTo())
                .updatedAt(entity.getUpdatedAt())
                .guestToken(entity.getGuestToken())
                .guestTokenExpiresAt(entity.getGuestTokenExpiresAt())
                .build();
    }

    private ParkingRecordEntity mapToEntity(ParkingRecord domain) {
        return ParkingRecordEntity.builder()
                .id(domain.getId())
                .ticketNumber(domain.getTicketNumber())
                .carNumber(domain.getCarNumber())
                .customerName(domain.getCustomerName())
                .phoneNumber(domain.getPhoneNumber())
                .parkingArea(domain.getParkingArea())
                .entryTime(domain.getEntryTime())
                .exitTime(domain.getExitTime())
                .status(domain.getStatus())
                .affiliationId(domain.getAffiliationId())
                .registeredBy(domain.getRegisteredBy())
                .exitRequestedBy(domain.getExitRequestedBy())
                .exitAssignedTo(domain.getExitAssignedTo())
                .guestToken(domain.getGuestToken())
                .guestTokenExpiresAt(domain.getGuestTokenExpiresAt())
                .build();
    }
}
