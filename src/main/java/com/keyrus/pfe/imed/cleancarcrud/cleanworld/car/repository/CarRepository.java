package com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.repository;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.model.Car;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Optional;

public interface CarRepository {

    Collection<Car> findAllCars();

    Optional<Car> findCarByPlateNumber(String plateNumber);

    Collection<Car> findAllCarsByType(String type);

    Collection<Car> findAllCarsByReleaseYear(int releaseYear);

    Either<? extends RepositoryCarError, Car> saveCar(Car car);

    Either<? extends RepositoryCarError, Car> updateCar(Car car);

    Optional<Car> deleteCar(Car car);

    Optional<Car> deleteCar(String plateNumber);

    Collection<Car> deleteAll();

    sealed interface RepositoryCarError {

        String message();

        record CarWithPlateNumberAlreadyExistError(String message) implements RepositoryCarError {
            public CarWithPlateNumberAlreadyExistError() {
                this("");
            }
        }

        record CarWithPlateNumberNotExistError(String message) implements RepositoryCarError {
            public CarWithPlateNumberNotExistError() {
                this("");
            }
        }

        record NullParameterError(String message) implements RepositoryCarError {
            public NullParameterError() {
                this("");
            }
        }
    }

}
