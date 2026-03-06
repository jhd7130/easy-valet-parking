package com.easyvalet.backend.adapter.out.persistence;

import com.easyvalet.backend.domain.ParkingRecord;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "parking_records")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ParkingRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ticketNumber;

    @Column(nullable = false)
    private String carNumber;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String parkingArea;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParkingRecord.Status status;

    private Long affiliationId;

    private String registeredBy;

    private String exitRequestedBy;

    private String exitAssignedTo;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(unique = true)
    private String guestToken;

    private LocalDateTime guestTokenExpiresAt;

    @PrePersist
    public void generateTicketNumber() {
        if (this.ticketNumber == null || this.ticketNumber.isEmpty()) {
            this.ticketNumber = "T-" + System.currentTimeMillis();
        }
    }
}
