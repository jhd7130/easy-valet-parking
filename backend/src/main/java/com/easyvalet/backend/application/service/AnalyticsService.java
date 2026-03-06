package com.easyvalet.backend.application.service;

import com.easyvalet.backend.application.port.out.ParkingRepositoryPort;
import com.easyvalet.backend.application.port.out.UserRepositoryPort;
import com.easyvalet.backend.domain.ParkingRecord;
import com.easyvalet.backend.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ParkingRepositoryPort parkingRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    @Transactional(readOnly = true)
    public Map<String, Object> getDailySummary(String userEmail) {
        User user = userRepositoryPort.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ParkingRecord> records = parkingRepositoryPort.findByAffiliationId(user.getAffiliationId());
        LocalDate today = LocalDate.now();

        List<ParkingRecord> todayRecords = records.stream()
                .filter(r -> r.getEntryTime() != null && r.getEntryTime().toLocalDate().equals(today))
                .collect(Collectors.toList());

        Map<String, Object> summary = new HashMap<>();
        summary.put("date", today.toString());
        summary.put("total", todayRecords.size());
        summary.put("parked", todayRecords.stream().filter(r -> r.getStatus() == ParkingRecord.Status.PARKED).count());
        summary.put("requested", todayRecords.stream().filter(r -> r.getStatus() == ParkingRecord.Status.REQUESTED).count());
        summary.put("exited", todayRecords.stream().filter(r -> r.getStatus() == ParkingRecord.Status.EXITED).count());

        return summary;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAverageDuration(String userEmail) {
        User user = userRepositoryPort.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ParkingRecord> records = parkingRepositoryPort.findByAffiliationId(user.getAffiliationId());

        List<ParkingRecord> exitedRecords = records.stream()
                .filter(r -> r.getStatus() == ParkingRecord.Status.EXITED && r.getEntryTime() != null && r.getExitTime() != null)
                .collect(Collectors.toList());

        double avgMinutes = exitedRecords.stream()
                .mapToLong(r -> Duration.between(r.getEntryTime(), r.getExitTime()).toMinutes())
                .average()
                .orElse(0.0);

        Map<String, Object> result = new HashMap<>();
        result.put("averageParkingMinutes", Math.round(avgMinutes));
        result.put("totalExited", exitedRecords.size());

        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getStaffPerformance(String userEmail) {
        User user = userRepositoryPort.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ParkingRecord> records = parkingRepositoryPort.findByAffiliationId(user.getAffiliationId());

        Map<String, Long> registeredCounts = records.stream()
                .filter(r -> r.getRegisteredBy() != null)
                .collect(Collectors.groupingBy(ParkingRecord::getRegisteredBy, Collectors.counting()));

        Map<String, Long> exitCounts = records.stream()
                .filter(r -> r.getExitAssignedTo() != null)
                .collect(Collectors.groupingBy(ParkingRecord::getExitAssignedTo, Collectors.counting()));

        java.util.Set<String> allStaff = new java.util.HashSet<>();
        allStaff.addAll(registeredCounts.keySet());
        allStaff.addAll(exitCounts.keySet());

        return allStaff.stream().map(name -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("staffName", name);
            entry.put("registered", registeredCounts.getOrDefault(name, 0L));
            entry.put("exitsCompleted", exitCounts.getOrDefault(name, 0L));
            return entry;
        }).collect(Collectors.toList());
    }
}
