package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.repository;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.model.Car;
import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.repository.CarRepository;
import io.vavr.control.Either;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public final class InMemoryCarRepository implements CarRepository {

    private static final InMemoryCarRepository instance = new InMemoryCarRepository();

    public static InMemoryCarRepository getInstance() {
        return instance;
    }

    private InMemoryCarRepository() {
    }

    private final Collection<Car> cars = new HashSet<>();

    @Override
    public Collection<Car> findAllCars() {
        return Collections.unmodifiableCollection(cars);
    }

    @Override
    public Optional<Car> findCarByPlateNumber(final String plateNumber) {
        final var carToReturn = cars.stream()
                .filter(car -> car.getPlatNumber().equals(plateNumber))
                .findFirst();
        System.out.println("carToReturn = " + carToReturn);
        return carToReturn;
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
    public Either<? extends RepositoryCarError, Car> saveCar(final Car car) {
        System.out.println("car to save in repo" + car);
        return applyOnCarIfExistOrNot(
                car,
                (carsCollection, carFound) -> Either.left(new RepositoryCarError.CarWithPlateNumberAlreadyExistError()),
                carsCollection -> saveCar(carsCollection, car)
        );
    }

    @Override
    public Either<? extends RepositoryCarError, Car> updateCar(final Car car) {
        return applyOnCarIfExistOrNot(
                car,
                this::updateCar,
                carsCollection -> Either.left(new RepositoryCarError.CarWithPlateNumberNotExistError())
        );
    }

    @Override
    public Optional<Car> deleteCar(final Car car) {
        if (Objects.isNull(car))
            return Optional.empty();
        return deleteCar(car.getPlatNumber());
    }

    @Override
    public Optional<Car> deleteCar(final String plateNumber) {
        if (Objects.isNull(plateNumber))
            return Optional.empty();
        else if (carExist(plateNumber)) {
            final var removedCar = findCarByPlateNumber(plateNumber).get();
            cars.remove(removedCar);
            return Optional.of(removedCar);
        } else
            return Optional.empty();
    }

    @Override
    public Collection<Car> deleteAll() {
        final var carsToReturn = new HashSet<>(cars);
        cars.clear();
        System.out.println("carsToReturn = " + carsToReturn);
        return carsToReturn;
    }

    private Collection<Car> findAllCarsByCriteria(final Predicate<Car> criteria) {
        return cars.stream()
                .filter(criteria)
                .toList();
    }

    private Either<? extends RepositoryCarError, Car> saveCar(
            final Collection<Car> carsCollection,
            final Car car
    ) {

        System.out.println("carsCollection before save = " + carsCollection);
        carsCollection.add(car);
        System.out.println("carsCollection after save = " + carsCollection);
        final Either<? extends RepositoryCarError, Car> x = Either.right(car);
        System.out.println("save car either right " + x);
        return x;
    }

    private Either<? extends RepositoryCarError, Car> updateCar(
            final Collection<Car> carsCollection,
            final Car car
    ) {
        System.out.println("carsCollection before updatee= " + carsCollection);
        carsCollection.remove(findCarByPlateNumber(car.getPlatNumber()).get());
        carsCollection.add(car);
        System.out.println("carsCollection after updatee= " + carsCollection);
        return Either.right(car);
    }

    private boolean carExist(final String plateNumber) {
        return cars.stream()
                .map(Car::getPlatNumber)
                .anyMatch(plateNumber::equals);
    }

    private Either<? extends RepositoryCarError, Car> applyOnCarIfExistOrNot(
            final Car car,
            final BiFunction<Collection<Car>, Car, Either<? extends RepositoryCarError, Car>> ifCarExist,
            final Function<Collection<Car>, Either<? extends RepositoryCarError, Car>> ifCarNotExist
    ) {
        System.out.println("car variable = " + car);
        if (Objects.isNull(car))
            return Either.left(new RepositoryCarError.NullParameterError());
        else if (carExist(car.getPlatNumber()))
            return ifCarExist.apply(cars, car);
        else
            return ifCarNotExist.apply(cars);
    }
}