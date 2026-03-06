package com.easyvalet.backend.adapter.in.web;

import com.easyvalet.backend.domain.ParkingRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ParkingDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterRequest {
        private String carNumber;
        private String customerName;
        private String phoneNumber;
        private String parkingArea;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String ticketNumber;
        private String carNumber;
        private String customerName;
        private String phoneNumber;
        private String parkingArea;
        private LocalDateTime entryTime;
        private LocalDateTime exitTime;
        private ParkingRecord.Status status;
        private String registeredBy;
        private String exitRequestedBy;
        private String exitAssignedTo;
        private String guestUrl;
        private String qrCodeBase64;

        public static Response from(ParkingRecord record) {
            return Response.builder()
                    .id(record.getId())
                    .ticketNumber(record.getTicketNumber())
                    .carNumber(record.getCarNumber())
                    .customerName(record.getCustomerName())
                    .phoneNumber(record.getPhoneNumber())
                    .parkingArea(record.getParkingArea())
                    .entryTime(record.getEntryTime())
                    .exitTime(record.getExitTime())
                    .status(record.getStatus())
                    .registeredBy(record.getRegisteredBy())
                    .exitRequestedBy(record.getExitRequestedBy())
                    .exitAssignedTo(record.getExitAssignedTo())
                    .guestUrl(record.getGuestToken() != null ? "/guest?token=" + record.getGuestToken() : null)
                    .build();
        }
    }
}
