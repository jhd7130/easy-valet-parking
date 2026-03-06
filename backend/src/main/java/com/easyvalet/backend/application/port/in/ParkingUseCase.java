package com.easyvalet.backend.application.port.in;

import com.easyvalet.backend.adapter.in.web.ParkingDto;
import com.easyvalet.backend.domain.ParkingRecord;
import java.util.List;

public interface ParkingUseCase {
    ParkingRecord registerParking(ParkingDto.RegisterRequest request, String userEmail);

    List<ParkingRecord> getAllParkingRecords(Long affiliationId);

    List<ParkingRecord> getActiveParkingRecords(Long affiliationId);

    ParkingRecord getParkingRecord(Long id);

    ParkingRecord getParkingRecordByTicketNumber(String ticketNumber);

    ParkingRecord requestExit(Long id, String userEmail);

    ParkingRecord acceptExit(Long id, String userEmail);

    ParkingRecord completeExit(Long id, String userEmail);
}
