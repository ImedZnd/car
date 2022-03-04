package com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.service;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.model.Car;
import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.repository.CarRepository;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public final class CarService {

    private final CarRepository carRepository;

    private CarService(final CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    private static CarService instance;

    public static synchronized CarService getInstance(final CarRepository carRepository) {
        if (Objects.isNull(instance))
            instance = new CarService(carRepository);
        return instance;
    }

    public Collection<Car> getAllCars() {
        return carRepository.findAllCars();
    }

    public Optional<Car> getCarByPlatNumber(final String platNumber) {
        return carRepository.findCarByPlateNumber(platNumber);
    }

    public Collection<Car> getAllCarsByType(final String type) {
        return carRepository.findAllCarsByType(type);
    }

    public Collection<Car> getAllCarsByReleaseYear(final int releaseYear) {
        return carRepository.findAllCarsByReleaseYear(releaseYear);
    }

    public Either<? extends ServiceCarError, Car> saveCar(final Car car) {
        return
                carRepository
                        .saveCar(car)
                        .mapLeft(this::carRepositoryErrorToCarServiceError);
    }

    public Either<? extends ServiceCarError, Car> updateCar(final Car car) {
        return carRepository
                .updateCar(car)
                .mapLeft(this::carRepositoryErrorToCarServiceError);
    }

    public Optional<Car> deleteCar(final Car car) {
        return carRepository.deleteCar(car);
    }

    public Collection<Car> deleteAllCars() {
        return carRepository.deleteAll();
    }

    public Optional<Car> deleteCar(final String platNumber) {
        return carRepository.deleteCar(platNumber);
    }

    private ServiceCarError carRepositoryErrorToCarServiceError(final CarRepository.RepositoryCarError repositoryCarError) {
        if (repositoryCarError instanceof CarRepository.RepositoryCarError.CarWithPlateNumberAlreadyExistError carRepositoryError)
            return new CarService.ServiceCarError.CarWithPlateNumberAlreadyExistError(carRepositoryError.message());
        if (repositoryCarError instanceof CarRepository.RepositoryCarError.CarWithPlateNumberNotExistError carRepositoryError)
            return new CarService.ServiceCarError.CarWithPlateNumberNotExistError(carRepositoryError.message());
        if (repositoryCarError instanceof CarRepository.RepositoryCarError.NullParameterError carRepositoryError)
            return new CarService.ServiceCarError.CarWithNullParameterError(carRepositoryError.message());
        return new CarService.ServiceCarError.UnknownError(repositoryCarError.message());
    }

    public sealed interface ServiceCarError {

        String message();

        record CarWithPlateNumberAlreadyExistError(String message) implements ServiceCarError {
            public CarWithPlateNumberAlreadyExistError() {
                this("");
            }
        }

        record CarWithPlateNumberNotExistError(String message) implements ServiceCarError {
            public CarWithPlateNumberNotExistError() {
                this("");
            }
        }

        record CarWithNullParameterError(String message) implements ServiceCarError {
            public CarWithNullParameterError() {
                this("");
            }
        }

        record UnknownError(String message) implements ServiceCarError {
            public UnknownError() {
                this("");
            }
        }
    }
}
