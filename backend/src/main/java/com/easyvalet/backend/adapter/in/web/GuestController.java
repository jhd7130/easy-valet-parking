package com.easyvalet.backend.adapter.in.web;

import com.easyvalet.backend.application.port.out.ParkingRepositoryPort;
import com.easyvalet.backend.application.service.ParkingEventPublisher;
import com.easyvalet.backend.domain.ParkingRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/guest")
@RequiredArgsConstructor
public class GuestController {

    private final ParkingRepositoryPort parkingRepositoryPort;
    private final ParkingEventPublisher parkingEventPublisher;

    @GetMapping("/status")
    public ResponseEntity<GuestDto.StatusResponse> getStatus(@RequestParam String token) {
        ParkingRecord record = findValidRecord(token);
        return ResponseEntity.ok(GuestDto.StatusResponse.from(record));
    }

    @PostMapping("/request-exit")
    public ResponseEntity<GuestDto.StatusResponse> requestExit(@RequestParam String token) {
        ParkingRecord record = findValidRecord(token);

        if (record.getStatus() != ParkingRecord.Status.PARKED) {
            throw new RuntimeException("Exit already requested or completed");
        }

        record.requestExit("GUEST");
        ParkingRecord saved = parkingRepositoryPort.save(record);
        parkingEventPublisher.publishUpdate(saved.getAffiliationId(), "parking-update", saved);
        return ResponseEntity.ok(GuestDto.StatusResponse.from(saved));
    }

    private ParkingRecord findValidRecord(String token) {
        ParkingRecord record = parkingRepositoryPort.findByGuestToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (record.getGuestTokenExpiresAt() != null
                && record.getGuestTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        return record;
    }
}
