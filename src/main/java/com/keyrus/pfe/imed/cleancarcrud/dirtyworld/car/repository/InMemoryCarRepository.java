package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.repository;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.model.Car;
import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.repository.CarRepository;
import com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.event.setting.CarEventSettings;
import io.vavr.control.Either;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class InMemoryCarRepository implements CarRepository {


    private static InMemoryCarRepository instance = null;

    public static synchronized InMemoryCarRepository instance(
            final RabbitTemplate rabbitTemplate,
            final CarEventSettings carEventSettings
    ) {
        if (Objects.isNull(instance))
            instance =
                    new InMemoryCarRepository(
                            rabbitTemplate,
                            carEventSettings
                    );
        return instance;
    }

    @Autowired
    private final RabbitTemplate rabbitTemplate;
    private final CarEventSettings carEventSettings;

    private InMemoryCarRepository(
            @Autowired final RabbitTemplate rabbitTemplate,
            final CarEventSettings carEventSettings
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.carEventSettings = carEventSettings;
    }

    private final Collection<Car> cars = new HashSet<>();

    @Override
    public Collection<Car> findAllCars() {
        return
                cars.stream()
                        .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Optional<Car> findCarByPlateNumber(final String plateNumber) {
        return
                cars.stream()
                        .filter(car -> car.getPlatNumber().equals(plateNumber))
                        .findFirst();
    }

    @Override
    public Collection<Car> findAllCarsByType(final String type) {
        return
                findAllCarsByCriteria(
                        car -> car.getType().equals(type)
                );
    }

    @Override
    public Collection<Car> findAllCarsByReleaseYear(final int releaseYear) {
        return
                findAllCarsByCriteria(
                        car -> car.getReleaseDate().getYear() == releaseYear
                );
    }

    @Override
    public Either<? extends RepositoryCarError, Car> saveCar(final Car car) {
        return
                applyOnCarIfExistOrNot(
                        car,
                        (carsCollection, carFound) -> Either.left(new RepositoryCarError.CarWithPlateNumberAlreadyExistError()),
                        carsCollection -> saveCar(carsCollection, car)
                );
    }

    @Override
    public Either<? extends RepositoryCarError, Car> updateCar(final Car car) {
        System.out.println("car = " + car);
        return
                applyOnCarIfExistOrNot(
                        car,
                        this::updateCar,
                        carsCollection -> Either.left(new RepositoryCarError.CarWithPlateNumberNotExistError())
                );
    }

    @Override
    public Optional<Car> deleteCar(final Car car) {
        if (Objects.isNull(car))
            return Optional.empty();
        return
                deleteCar(
                        car.getPlatNumber()
                );
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
        final var carsToReturn = findAllCars();
        cars.clear();
        return carsToReturn;
    }

    @Override
    public void publishSaveCar(final Car car) {
        if (!Objects.isNull(car))
            this.publishEvent(
                car.getPlatNumber(),
                carEventSettings.save().exchange(),
                carEventSettings.save().routingkey()
        );
    }

    @Override
    public void publishUpdateCar(final Car car) {
        if (!Objects.isNull(car))
            this.publishEvent(
                car.getPlatNumber(),
                carEventSettings.update().exchange(),
                carEventSettings.update().routingkey()
        );
    }

    @Override
    public void publishDeleteCar(final Car car) {
        if (!Objects.isNull(car))
            this.publishEvent(
                car.getPlatNumber(),
                carEventSettings.delete().exchange(),
                carEventSettings.delete().routingkey()
        );
    }

    private void publishEvent(
            final String plateNumber,
            final String exchange,
            final String routingKey
    ) {
        rabbitTemplate.convertAndSend(
                exchange,
                routingKey,
                plateNumber
        );
    }

    private Collection<Car> findAllCarsByCriteria(final Predicate<Car> criteria) {
        return
                cars.stream()
                        .filter(criteria)
                        .toList();
    }

    private Either<? extends RepositoryCarError, Car> saveCar(
            final Collection<Car> carsCollection,
            final Car car
    ) {
        carsCollection.add(car);
        return Either.right(car);
    }

    private Either<? extends RepositoryCarError, Car> updateCar(
            final Collection<Car> carsCollection,
            final Car car
    ) {
        carsCollection.remove(findCarByPlateNumber(car.getPlatNumber()).get());
        carsCollection.add(car);
        return Either.right(car);
    }

    private boolean carExist(final String plateNumber) {
        return
                cars.stream()
                        .map(Car::getPlatNumber)
                        .anyMatch(plateNumber::equals);
    }

    private Either<? extends RepositoryCarError, Car> applyOnCarIfExistOrNot(
            final Car car,
            final BiFunction<Collection<Car>, Car, Either<? extends RepositoryCarError, Car>> ifCarExist,
            final Function<Collection<Car>, Either<? extends RepositoryCarError, Car>> ifCarNotExist
    ) {
        if (Objects.isNull(car))
            return
                    Either.left(new RepositoryCarError.NullParameterError());
        else if (carExist(car.getPlatNumber()))
            return
                    ifCarExist.apply(
                            cars,
                            car
                    );
        else
            return
                    ifCarNotExist.apply(cars);
    }
}