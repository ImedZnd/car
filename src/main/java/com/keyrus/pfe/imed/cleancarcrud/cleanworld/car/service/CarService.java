package com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.service;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.model.Car;
import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.repository.CarRepository;
import io.vavr.control.Either;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class CarService {

    private static CarRepository carRepository ;
    private static CarService carService;

    private CarService(final CarRepository carRepository) {
        CarService.carRepository = carRepository;
    }

    public static synchronized CarService getInstance(final CarRepository carRepository) {
        if (Objects.isNull(carService)) {
            carService = new CarService(carRepository);
        }
        return carService;
    }

    public Collection<Car> getAllCars() {
        return carRepository.findAllCars();
    }

    public Optional<Car> getCarByPlatNumber(String platNumber) {
        return carRepository.findCarByPlateNumber(platNumber);
    }

    public Collection<Car> getAllCarsByType(String type) {
        return carRepository.findAllCarsByType(type);
    }

    public Collection<Car> getAllCarsByReleaseYear(int releaseYear) {
        return carRepository.findAllCarsByReleaseYear(releaseYear);
    }

    public Either<? extends CarRepository.RepositoryCarError, Car> saveCar(Car car) {
        return carRepository.saveCar(car);
    }

    public Either<? extends CarRepository.RepositoryCarError, Car> updateCar(Car car) {
        return carRepository.updateCar(car);
    }

    public Optional<Car> deleteCar(Car car) {
        return carRepository.deleteCar(car);
    }

    public Optional<Car> deleteCarByPlatNumber(String platNumber) {
        return carRepository.deleteCar(platNumber);
    }
}
