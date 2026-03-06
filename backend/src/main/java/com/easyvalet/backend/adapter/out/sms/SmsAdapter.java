package com.easyvalet.backend.adapter.out.sms;

import com.easyvalet.backend.application.port.out.SmsPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsAdapter implements SmsPort {

    @Override
    public void sendParkingNotification(String phoneNumber, String ticketNumber, String carNumber, String guestUrl) {
        log.info("=== SMS Notification ===");
        log.info("To: {}", phoneNumber);
        log.info("Message: Your vehicle ({}) has been parked. Ticket: {}", carNumber, ticketNumber);
        log.info("Request exit here: {}", guestUrl);
        log.info("========================");
    }

    @Override
    public void sendExitRequestNotification(String phoneNumber, String ticketNumber, String carNumber) {
        log.info("=== SMS Notification ===");
        log.info("To: {}", phoneNumber);
        log.info("Message: Exit requested for your vehicle ({}). Ticket: {}. We'll bring your car shortly.", carNumber,
                ticketNumber);
        log.info("========================");
    }

    @Override
    public void sendExitCompleteNotification(String phoneNumber, String ticketNumber, String carNumber) {
        log.info("=== SMS Notification ===");
        log.info("To: {}", phoneNumber);
        log.info("Message: Your vehicle ({}) is ready for pickup. Ticket: {}", carNumber, ticketNumber);
        log.info("========================");
    }
}
