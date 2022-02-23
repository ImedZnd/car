package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.repository;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.model.Car;
import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.repository.CarRepository;
import io.vavr.control.Either;

import java.util.*;
import java.util.function.Predicate;

public final class InMemoryCarRepository implements CarRepository {

    private static final InMemoryCarRepository instance = new InMemoryCarRepository();

    public static InMemoryCarRepository getInstance(){
        return instance;
    }

    private InMemoryCarRepository(){
    }

    private final Collection<Car> cars = new HashSet<>();

    @Override
    public Collection<Car> findAllCars() {
        return Collections.unmodifiableCollection(cars);
    }

    @Override
    public Optional<Car> findCarByPlateNumber(final String plateNumber) {
        return cars.stream()
                .filter(car -> car.getPlatNumber().equals(plateNumber))
                .findFirst();
    }

    @Override
    public Collection<Car> findAllCarsByType(final String type) {
        return findAllCarsByCriteria(car -> car.getType().equals(type));
    }

    @Override
    public Collection<Car> findAllCarsByReleaseYear(final int releaseYear) {
        return findAllCarsByCriteria(car -> car.getReleaseDate().getYear() == releaseYear);
    }

    @Override
    public Either<? extends RepositoryCarError, Car> saveCar(Car car) {
        if (Objects.isNull(car))
            return Either.left(new RepositoryCarError.NullParameterError());
        else if (carExist(car.getPlatNumber()))
            return Either.left(new RepositoryCarError.CarWithPlateNumberAlreadyExistError());
        else {
            cars.add(car);
            return Either.right(car);
        }
    }

    @Override
    public Either<? extends RepositoryCarError, Car> updateCar(Car car) {
        if (Objects.isNull(car))
            return Either.left(new RepositoryCarError.NullParameterError());
        else if (carExist(car.getPlatNumber())) {
            final var removedCar = findCarByPlateNumber(car.getPlatNumber()).get();
            cars.remove(removedCar);
            cars.add(car);
            return Either.right(car);
        } else {
            return Either.left(new RepositoryCarError.CarWithPlateNumberNotExistError());
        }
    }

    @Override
    public Optional<Car> deleteCar(Car car) {
        if (Objects.isNull(car))
            return Optional.empty();
        return deleteCar(car.getPlatNumber());
    }

    @Override
    public Optional<Car> deleteCar(String plateNumber) {
        if (Objects.isNull(plateNumber))
            return Optional.empty();
        else if (carExist(plateNumber)) {
            final var removedCar = findCarByPlateNumber(plateNumber).get();
            cars.remove(removedCar);
            return Optional.of(removedCar);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Car> deleteAll() {
        final var carsToReturn = findAllCars();
        cars.clear();
        return carsToReturn;
    }

    private Collection<Car> findAllCarsByCriteria(final Predicate<Car> criteria) {
        return cars.stream()
                .filter(criteria)
                .toList();
    }

    private boolean carExist(String plateNumber) {
        return cars.stream()
                .map(Car::getPlatNumber)
                .anyMatch(plateNumber::equals);
    }
}
