package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.rest.handler;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.model.Car;
import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.service.CarService;
import com.keyrus.pfe.imed.cleancarcrud.dirtyworld.dto.CarDTO;
import io.vavr.control.Either;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Optional;

public class CarRestHandler {

    private final CarService carService;

    private CarRestHandler(final CarService carService) {
        this.carService = carService;
    }

    private static CarRestHandler instance;

    public static synchronized CarRestHandler instance(final CarService carService) {
        if (instance == null)
            instance = new CarRestHandler(carService);
        return instance;
    }

    public Mono<ServerResponse> getAllCars(final ServerRequest serverRequest) {
        return returnCollectionOfCarsByCriteria(carService.getAllCars());
    }

    public Mono<ServerResponse> getCarByPlatNumber(final ServerRequest serverRequest) {
        final var plateNumber = serverRequest.pathVariable("platenumber");
        return returnCarInOptional(carService.getCarByPlatNumber(plateNumber));
    }

    public Mono<ServerResponse> getAllCarsByType(final ServerRequest serverRequest) {
        final var type = serverRequest.pathVariable("type");
        return returnCollectionOfCarsByCriteria(carService.getAllCarsByType(type));
    }

    public Mono<ServerResponse> getAllCarsByReleaseYear(final ServerRequest serverRequest) {
        final var releaseYear = Integer.parseInt(serverRequest.pathVariable("releaseYear"));
        return returnCollectionOfCarsByCriteria(carService.getAllCarsByReleaseYear(releaseYear));
    }

    public Mono<ServerResponse> saveCar(final ServerRequest serverRequest) {
        final var car = serverRequest.bodyToMono(Car.class).block();
        return applyOnCar(carService.saveCar(car));
    }

    public Mono<ServerResponse> updateCar(final ServerRequest serverRequest) {
        final var carDto = serverRequest.bodyToMono(CarDTO.class).block();
        final var date = carDto.getReleaseDate().toLocalDate().get();
        final var car = Car.of(
                        carDto.getPlatNumber(),
                        carDto.getType(),
                        date
                )
                .get();
        return applyOnCar(carService.updateCar(car));
    }

    public Mono<ServerResponse> deleteCar(final ServerRequest serverRequest) {
        final var car = serverRequest.bodyToMono(Car.class).block();
        return returnCarInOptional(carService.deleteCar(car));
    }

    public Mono<ServerResponse> deleteAllCars(final ServerRequest serverRequest) {
        return returnCollectionOfCarsByCriteria(carService.deleteAllCars());

    }

    public Mono<ServerResponse> deleteCarByPlatNumber(final ServerRequest serverRequest) {
        final var platenumber = serverRequest.pathVariable("platenumber");
        return returnCarInOptional(carService.deleteCarByPlatNumber(platenumber));
    }

    private Mono<ServerResponse> applyOnCar(final Either<? extends CarService.ServiceCarError, Car> result) {
        if (result.isRight())
            return serverResponseIsOkReturn(result);
        else
            return serverResponseIsNotOkReturn(result);
    }

    private Mono<ServerResponse> returnCarInOptional(final Optional<Car> resultCar) {
        if (resultCar.isPresent())
            return serverResponseIsOkReturn(resultCar);
        else
            return serverResponseIsNotOkReturn(resultCar);
    }

    private Mono<ServerResponse> returnCollectionOfCarsByCriteria(final Collection<Car> cars) {
        return serverResponseIsOkReturn(cars
                .stream()
                .map(CarDTO::new)
                .toList());
    }

    private Mono<ServerResponse> serverResponseIsOkReturn(final Object object) {
        return
                ServerResponse
                        .ok()
                        .bodyValue(object);
    }

    private Mono<ServerResponse> serverResponseIsNotOkReturn(final Object object) {
        return
                ServerResponse
                        .badRequest()
                        .bodyValue(object);
    }

}
