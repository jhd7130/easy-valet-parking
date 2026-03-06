package com.easyvalet.backend.application.service;

import com.easyvalet.backend.application.port.in.CustomerUseCase;
import com.easyvalet.backend.application.port.out.CarRepositoryPort;
import com.easyvalet.backend.application.port.out.CustomerRepositoryPort;
import com.easyvalet.backend.domain.Car;
import com.easyvalet.backend.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService implements CustomerUseCase {

    private final CustomerRepositoryPort customerRepositoryPort;
    private final CarRepositoryPort carRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findCustomersByCarNumber(String carNumber) {
        return customerRepositoryPort.findByCarNumber(carNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findCustomersByPhoneNumber(String phoneNumber) {
        return customerRepositoryPort.findByPhoneNumber(phoneNumber);
    }

    @Override
    @Transactional
    public void registerCustomerCar(String carNumber, String customerName, String phoneNumber) {
        Optional<Customer> customerOpt = customerRepositoryPort.findByNameAndPhoneNumber(customerName, phoneNumber);
        Customer customer;
        if (customerOpt.isPresent()) {
            customer = customerOpt.get();
        } else {
            customer = Customer.builder()
                    .name(customerName)
                    .phoneNumber(phoneNumber)
                    .build();
        }

        boolean alreadyHasCar = customer.getCars().stream()
                .anyMatch(car -> car.getCarNumber().equals(carNumber));

        if (!alreadyHasCar) {
            Car car = carRepositoryPort.findByCarNumber(carNumber)
                    .orElseGet(() -> Car.builder().carNumber(carNumber).build());
            customer.getCars().add(car);
            customerRepositoryPort.save(customer);
        }
    }
}
