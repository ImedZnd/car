package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.contoller;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.model.Car;
import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.repository.CarRepository;
import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.service.CarService;
import com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.repository.InMemoryCarRepository;
import io.vavr.control.Either;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
public final class CarController {

    private static final InMemoryCarRepository inMemoryCarRepositoryInstance = InMemoryCarRepository.getInstance();

    private final CarService carServiceInstance = CarService.getInstance(inMemoryCarRepositoryInstance);

    @GetMapping("/")
    public Collection<Car> getAllCars(){
        return carServiceInstance.getAllCars();
    }

    @PostMapping("/platenumber/{platenumber}")
    public Optional<Car> getCarByPlatNumber(@PathVariable String platenumber) {
        return carServiceInstance.getCarByPlatNumber(platenumber);
    }

    @GetMapping("/type/{type}")
    public Collection<Car> getAllCarsByType(@PathVariable String type){
        return carServiceInstance.getAllCarsByType(type);
    }

    @GetMapping("/releaseYear/{releaseYear}")
    public Collection<Car> getAllCarsByReleaseYear(@PathVariable int releaseYear){
        return carServiceInstance.getAllCarsByReleaseYear(releaseYear);
    }

    @PostMapping("/save")
    public Either<? extends CarService.ServiceCarError, Car> saveCar(Car car) {
        return carServiceInstance.saveCar(car);
    }

    @PutMapping("/update")
    public Either<? extends CarService.ServiceCarError, Car> updateCar(@RequestBody Car car) {
        return carServiceInstance.updateCar(car);
    }

    @DeleteMapping("/delete")
    public Optional<Car> deleteCar(Car car) {
        return carServiceInstance.deleteCar(car);
    }

    @DeleteMapping("/deleteAll")
    public Collection<Car> deleteAllCars() {
        return carServiceInstance.deleteAllCars();
    }

    @DeleteMapping("/delete/{platenumber}")
    public Optional<Car> deleteCarByPlatNumber(@PathVariable String platenumber) {
        return carServiceInstance.deleteCarByPlatNumber(platenumber);
    }

}
