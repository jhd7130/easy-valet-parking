package com.easyvalet.backend.application.service;

import com.easyvalet.backend.adapter.in.web.ParkingDto;
import com.easyvalet.backend.application.port.in.ParkingUseCase;
import com.easyvalet.backend.application.port.out.ParkingRepositoryPort;
import com.easyvalet.backend.application.port.out.SmsPort;
import com.easyvalet.backend.domain.ParkingRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParkingService implements ParkingUseCase {

    private final ParkingRepositoryPort parkingRepositoryPort;
    private final com.easyvalet.backend.application.port.out.UserRepositoryPort userRepositoryPort;
    private final com.easyvalet.backend.application.port.in.CustomerUseCase customerUseCase;
    private final SmsPort smsPort;
    private final ParkingEventPublisher parkingEventPublisher;

    @Override
    @Transactional
    public ParkingRecord registerParking(ParkingDto.RegisterRequest request, String userEmail) {
        com.easyvalet.backend.domain.User user = userRepositoryPort.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ParkingRecord record = ParkingRecord.builder()
                .carNumber(request.getCarNumber())
                .customerName(request.getCustomerName())
                .phoneNumber(request.getPhoneNumber())
                .parkingArea(request.getParkingArea())
                .status(ParkingRecord.Status.PARKED)
                .affiliationId(user.getAffiliationId())
                .registeredBy(user.getNickname())
                .guestToken(UUID.randomUUID().toString().replace("-", ""))
                .guestTokenExpiresAt(LocalDateTime.now().plusHours(24))
                .build();

        ParkingRecord saved = parkingRepositoryPort.save(record);

        customerUseCase.registerCustomerCar(saved.getCarNumber(), saved.getCustomerName(), saved.getPhoneNumber());

        String guestUrl = "/guest?token=" + saved.getGuestToken();
        smsPort.sendParkingNotification(saved.getPhoneNumber(), saved.getTicketNumber(), saved.getCarNumber(), guestUrl);

        parkingEventPublisher.publishUpdate(saved.getAffiliationId(), "parking-update", saved);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingRecord> getAllParkingRecords(Long affiliationId) {
        if (affiliationId == null) {
            return parkingRepositoryPort.findAll();
        }
        return parkingRepositoryPort.findByAffiliationId(affiliationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParkingRecord> getActiveParkingRecords(Long affiliationId) {
        if (affiliationId == null) {
            return parkingRepositoryPort.findByStatusNot(ParkingRecord.Status.EXITED);
        }
        return parkingRepositoryPort.findByAffiliationIdAndStatusNot(affiliationId, ParkingRecord.Status.EXITED);
    }

    @Override
    @Transactional(readOnly = true)
    public ParkingRecord getParkingRecord(Long id) {
        return parkingRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Parking record not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public ParkingRecord getParkingRecordByTicketNumber(String ticketNumber) {
        return parkingRepositoryPort.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new RuntimeException("Parking record not found"));
    }

    @Override
    @Transactional
    public ParkingRecord requestExit(Long id, String userEmail) {
        com.easyvalet.backend.domain.User user = userRepositoryPort.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ParkingRecord record = getParkingRecord(id);
        record.requestExit(user.getNickname());
        ParkingRecord saved = parkingRepositoryPort.save(record);

        smsPort.sendExitRequestNotification(saved.getPhoneNumber(), saved.getTicketNumber(), saved.getCarNumber());

        parkingEventPublisher.publishUpdate(saved.getAffiliationId(), "parking-update", saved);

        parkingEventPublisher.publishUpdateExcluding(
                saved.getAffiliationId(), user.getId(), "exit-notification",
                java.util.Map.of(
                        "parkingId", saved.getId(),
                        "carNumber", saved.getCarNumber(),
                        "ticketNumber", saved.getTicketNumber(),
                        "requestedBy", user.getNickname()
                )
        );

        return saved;
    }

    @Override
    @Transactional
    public ParkingRecord acceptExit(Long id, String userEmail) {
        com.easyvalet.backend.domain.User user = userRepositoryPort.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ParkingRecord record = getParkingRecord(id);
        record.acceptExit(user.getNickname());
        ParkingRecord saved = parkingRepositoryPort.save(record);
        parkingEventPublisher.publishUpdate(saved.getAffiliationId(), "parking-update", saved);
        return saved;
    }

    @Override
    @Transactional
    public ParkingRecord completeExit(Long id, String userEmail) {
        com.easyvalet.backend.domain.User user = userRepositoryPort.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ParkingRecord record = getParkingRecord(id);
        // If not yet assigned, assign to the person completing
        if (record.getExitAssignedTo() == null) {
            record.acceptExit(user.getNickname());
        }
        record.completeExit();
        ParkingRecord saved = parkingRepositoryPort.save(record);

        smsPort.sendExitCompleteNotification(saved.getPhoneNumber(), saved.getTicketNumber(), saved.getCarNumber());

        parkingEventPublisher.publishUpdate(saved.getAffiliationId(), "parking-update", saved);
        return saved;
    }
}
