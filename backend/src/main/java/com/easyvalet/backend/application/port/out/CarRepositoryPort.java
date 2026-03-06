package com.easyvalet.backend.application.port.out;

import com.easyvalet.backend.domain.Car;
import java.util.Optional;

public interface CarRepositoryPort {
    Optional<Car> findByCarNumber(String carNumber);

    Car save(Car car);
}
