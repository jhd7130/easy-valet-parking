package com.easyvalet.backend.application.port.in;

import com.easyvalet.backend.domain.Customer;
import java.util.List;

public interface CustomerUseCase {
    List<Customer> findCustomersByCarNumber(String carNumber);

    List<Customer> findCustomersByPhoneNumber(String phoneNumber);

    void registerCustomerCar(String carNumber, String customerName, String phoneNumber);
}
