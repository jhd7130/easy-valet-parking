package com.easyvalet.backend.adapter.out.persistence;

import com.easyvalet.backend.application.port.out.CarRepositoryPort;
import com.easyvalet.backend.domain.Car;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CarPersistenceAdapter implements CarRepositoryPort {

    private final CarJpaRepository carJpaRepository;

    @Override
    public Optional<Car> findByCarNumber(String carNumber) {
        return carJpaRepository.findByCarNumber(carNumber).map(this::mapToDomain);
    }

    @Override
    public Car save(Car car) {
        CarEntity entity = mapToEntity(car);
        return mapToDomain(carJpaRepository.save(entity));
    }

    private Car mapToDomain(CarEntity entity) {
        return Car.builder()
                .id(entity.getId())
                .carNumber(entity.getCarNumber())
                .build();
    }

    private CarEntity mapToEntity(Car domain) {
        return CarEntity.builder()
                .id(domain.getId())
                .carNumber(domain.getCarNumber())
                .build();
    }
}
