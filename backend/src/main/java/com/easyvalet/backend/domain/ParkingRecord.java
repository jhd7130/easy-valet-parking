package com.easyvalet.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingRecord {
    private Long id;
    private String ticketNumber;
    private String carNumber;
    private String customerName;
    private String phoneNumber;
    private String parkingArea;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Status status;
    private Long affiliationId;
    private String registeredBy;
    private String exitRequestedBy;
    private String exitAssignedTo;
    private LocalDateTime updatedAt;
    private String guestToken;
    private LocalDateTime guestTokenExpiresAt;

    public enum Status {
        PARKED, REQUESTED, EXITED
    }

    public void requestExit(String requesterNickname) {
        this.status = Status.REQUESTED;
        this.exitRequestedBy = requesterNickname;
    }

    public void acceptExit(String assigneeNickname) {
        this.exitAssignedTo = assigneeNickname;
    }

    public void completeExit() {
        this.status = Status.EXITED;
        this.exitTime = LocalDateTime.now();
    }
}
