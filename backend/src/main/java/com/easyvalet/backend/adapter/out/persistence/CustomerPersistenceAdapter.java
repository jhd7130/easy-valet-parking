package com.easyvalet.backend.adapter.out.persistence;

import com.easyvalet.backend.application.port.out.CustomerRepositoryPort;
import com.easyvalet.backend.domain.Car;
import com.easyvalet.backend.domain.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomerPersistenceAdapter implements CustomerRepositoryPort {

        private final CustomerJpaRepository customerJpaRepository;
        private final CarJpaRepository carJpaRepository;

        @Override
        public List<Customer> findByCarNumber(String carNumber) {
                return customerJpaRepository.findByCarNumber(carNumber).stream()
                                .map(this::mapToDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public List<Customer> findByPhoneNumber(String phoneNumber) {
                return customerJpaRepository.findByPhoneNumber(phoneNumber).stream()
                                .map(this::mapToDomain)
                                .collect(Collectors.toList());
        }

        @Override
        public Optional<Customer> findByNameAndPhoneNumber(String name, String phoneNumber) {
                return customerJpaRepository.findByNameAndPhoneNumber(name, phoneNumber)
                                .map(this::mapToDomain);
        }

        @Override
        public Customer save(Customer customer) {
                CustomerEntity entity = mapToEntity(customer);
                return mapToDomain(customerJpaRepository.save(entity));
        }

        private Customer mapToDomain(CustomerEntity entity) {
                return Customer.builder()
                                .id(entity.getId())
                                .name(entity.getName())
                                .phoneNumber(entity.getPhoneNumber())
                                .cars(new ArrayList<>(entity.getCars().stream()
                                                .map(car -> Car.builder()
                                                                .id(car.getId())
                                                                .carNumber(car.getCarNumber())
                                                                .build())
                                                .collect(Collectors.toList())))
                                .build();
        }

        private CustomerEntity mapToEntity(Customer domain) {
                List<CarEntity> carEntities = domain.getCars().stream()
                                .map(car -> carJpaRepository.findByCarNumber(car.getCarNumber())
                                                .orElseGet(() -> CarEntity.builder()
                                                                .carNumber(car.getCarNumber())
                                                                .build()))
                                .collect(Collectors.toList());

                return CustomerEntity.builder()
                                .id(domain.getId())
                                .name(domain.getName())
                                .phoneNumber(domain.getPhoneNumber())
                                .cars(carEntities)
                                .build();
        }
}
