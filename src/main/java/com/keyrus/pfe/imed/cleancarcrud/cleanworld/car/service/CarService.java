package com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.service;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.model.Car;
import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.repository.CarRepository;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

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
        return operateOnCarRepositoryForEitherAndPublishEvent(
                it ->
                        it.saveCar(car)
                                .mapLeft(this::carRepositoryErrorToCarServiceError),
                it -> it.publishSaveCar(car)
        );
    }

    public Either<? extends ServiceCarError, Car> updateCar(final Car car) {
        return operateOnCarRepositoryForEitherAndPublishEvent(
                it ->
                        it.updateCar(car)
                                .mapLeft(this::carRepositoryErrorToCarServiceError),
                it -> it.publishUpdateCar(car)
        );
    }

    public Optional<Car> deleteCar(final Car car) {
        System.out.println("CarService.deleteCar");
        final var carIsDeleted = carRepository.deleteCar(car);
        System.out.println("carIsDeleted = " + carIsDeleted);
        carIsDeleted.ifPresent(carRepository::publishDeleteCar);
        return carIsDeleted;
    }

    public Collection<Car> deleteAllCars() {
        return carRepository.deleteAll();
    }

    public Optional<Car> deleteCar(final String platNumber) {
        final var carIsDeleted = carRepository.deleteCar(platNumber);
        carIsDeleted.ifPresent(carRepository::publishDeleteCar);
        return carIsDeleted;
    }

    private Either<? extends CarService.ServiceCarError, Car> operateOnCarRepositoryForEitherAndPublishEvent(
            Function<CarRepository, Either<? extends CarService.ServiceCarError, Car>> operationOnCarRepository,
            Consumer<CarRepository> publishEvent
    ) {
        final var operationOnCarRepositoryResult = operationOnCarRepository.apply(carRepository);
        if (operationOnCarRepositoryResult.isRight())
            publishEvent.accept(carRepository);
        return operationOnCarRepositoryResult;
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
