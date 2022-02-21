package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.repository;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.model.Car;
import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.repository.CarRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.InstanceOf;

record InMemoryCarRepositoryTest(
        InMemoryCarRepository inMemoryCarRepository) {


    @Test
    @DisplayName("save null car must not be valid")
    void save_null_car_must_not_be_valid() {
        final Car car = null;
        final var result =
                inMemoryCarRepository.saveCar(car)
                        .getLeft();
        Assertions.assertTrue(result instanceof CarRepository.RepositoryCarError.NullParameterError);
    }

}