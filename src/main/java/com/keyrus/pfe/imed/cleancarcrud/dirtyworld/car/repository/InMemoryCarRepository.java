package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.repository;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.model.Car;
import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.repository.CarRepository;
import io.vavr.control.Either;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;

public final class InMemoryCarRepository implements CarRepository {

    private final Collection<Car> cars = new ArrayList<>();

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
        return null;
    }

    @Override
    public Either<? extends RepositoryCarError, Car> updateCar(Car car) {
        return null;
    }

    @Override
    public Optional<Car> deleteCar(Car car) {
        return Optional.empty();
    }

    @Override
    public Optional<Car> deleteCar(String plateNumber) {
        return Optional.empty();
    }

    private Collection<Car> findAllCarsByCriteria(final Predicate<Car> criteria) {
        return cars.stream()
                .filter(criteria)
                .toList();
    }
}
