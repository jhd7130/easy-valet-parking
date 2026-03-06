package com.easyvalet.backend.adapter.in.web;

import com.easyvalet.backend.application.port.in.ParkingUseCase;
import com.easyvalet.backend.application.port.in.UserUseCase;
import com.easyvalet.backend.domain.ParkingRecord;
import com.easyvalet.backend.domain.User;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingUseCase parkingUseCase;
    private final UserUseCase userUseCase;

    @PostMapping
    public ResponseEntity<ParkingDto.Response> register(@RequestBody ParkingDto.RegisterRequest request,
            Authentication authentication) {
        ParkingRecord record = parkingUseCase.registerParking(request, authentication.getName());
        ParkingDto.Response response = ParkingDto.Response.from(record);
        response.setQrCodeBase64(generateQrCode(response.getGuestUrl()));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ParkingDto.Response>> getAll(
            Authentication authentication) {
        User user = userUseCase.getUserByEmail(authentication.getName());
        List<ParkingDto.Response> responses = parkingUseCase.getAllParkingRecords(user.getAffiliationId()).stream()
                .map(ParkingDto.Response::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ParkingDto.Response>> getActive(
            Authentication authentication) {
        User user = userUseCase.getUserByEmail(authentication.getName());
        List<ParkingDto.Response> responses = parkingUseCase.getActiveParkingRecords(user.getAffiliationId()).stream()
                .map(ParkingDto.Response::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingDto.Response> getOne(@PathVariable Long id) {
        ParkingRecord record = parkingUseCase.getParkingRecord(id);
        ParkingDto.Response response = ParkingDto.Response.from(record);
        response.setQrCodeBase64(generateQrCode(response.getGuestUrl()));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/request-exit")
    public ResponseEntity<ParkingDto.Response> requestExit(@PathVariable Long id,
            Authentication authentication) {
        ParkingRecord record = parkingUseCase.requestExit(id, authentication.getName());
        return ResponseEntity.ok(ParkingDto.Response.from(record));
    }

    @PostMapping("/{id}/accept-exit")
    public ResponseEntity<ParkingDto.Response> acceptExit(@PathVariable Long id,
            Authentication authentication) {
        ParkingRecord record = parkingUseCase.acceptExit(id, authentication.getName());
        return ResponseEntity.ok(ParkingDto.Response.from(record));
    }

    @PostMapping("/{id}/complete-exit")
    public ResponseEntity<ParkingDto.Response> completeExit(@PathVariable Long id,
            Authentication authentication) {
        ParkingRecord record = parkingUseCase.completeExit(id, authentication.getName());
        return ResponseEntity.ok(ParkingDto.Response.from(record));
    }

    private String generateQrCode(String guestUrl) {
        if (guestUrl == null) return null;
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(guestUrl, BarcodeFormat.QR_CODE, 200, 200);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            return null;
        }
    }
}
