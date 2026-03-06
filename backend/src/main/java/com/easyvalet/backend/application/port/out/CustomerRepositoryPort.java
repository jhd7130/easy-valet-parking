package com.easyvalet.backend.application.port.out;

import com.easyvalet.backend.domain.Customer;
import java.util.List;
import java.util.Optional;

public interface CustomerRepositoryPort {
    List<Customer> findByCarNumber(String carNumber);

    List<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByNameAndPhoneNumber(String name, String phoneNumber);

    Customer save(Customer customer);
}
