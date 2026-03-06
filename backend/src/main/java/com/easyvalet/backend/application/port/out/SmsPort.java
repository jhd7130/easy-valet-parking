package com.easyvalet.backend.application.port.out;

public interface SmsPort {
    void sendParkingNotification(String phoneNumber, String ticketNumber, String carNumber, String guestUrl);

    void sendExitRequestNotification(String phoneNumber, String ticketNumber, String carNumber);

    void sendExitCompleteNotification(String phoneNumber, String ticketNumber, String carNumber);
}
