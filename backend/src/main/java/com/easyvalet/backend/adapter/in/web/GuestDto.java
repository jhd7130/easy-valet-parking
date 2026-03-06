package com.easyvalet.backend.adapter.in.web;

import com.easyvalet.backend.domain.ParkingRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class GuestDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StatusResponse {
        private String maskedCarNumber;
        private String customerName;
        private ParkingRecord.Status status;
        private LocalDateTime entryTime;
        private String parkingArea;

        public static StatusResponse from(ParkingRecord record) {
            String carNumber = record.getCarNumber();
            String masked = carNumber.length() > 4
                    ? "****" + carNumber.substring(carNumber.length() - 4)
                    : carNumber;

            return StatusResponse.builder()
                    .maskedCarNumber(masked)
                    .customerName(record.getCustomerName())
                    .status(record.getStatus())
                    .entryTime(record.getEntryTime())
                    .parkingArea(record.getParkingArea())
                    .build();
        }
    }
}
