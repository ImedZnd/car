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
        System.out.println(" path variable plateNumber = " + serverRequest.pathVariable("plateNumber"));
        return returnCarInOptional(carService
                .getCarByPlatNumber(serverRequest.pathVariable("plateNumber")));
    }

    public Mono<ServerResponse> getAllCarsByType(final ServerRequest serverRequest) {
        return returnCollectionOfCarsByCriteria(carService
                .getAllCarsByType(serverRequest.pathVariable("type")));
    }

    public Mono<ServerResponse> getAllCarsByReleaseYear(final ServerRequest serverRequest) {
        final var releaseYear = Integer.parseInt(serverRequest.pathVariable("releaseYear"));
        return returnCollectionOfCarsByCriteria(carService.getAllCarsByReleaseYear(releaseYear));
    }

    public Mono<ServerResponse> saveCar(final ServerRequest serverRequest) {
        System.out.println("save car handler called");
        return serverRequest
                .bodyToMono(CarDTO.class)
                .map(CarDTO::toCar)
                .flatMap(it -> {
                    if (it.isRight()) {
                        final var car = carService.saveCar(it.get());
                        if (car.isRight())
                            return serverResponseIsOkReturn(new CarDTO(car.get()));
                        else return serverResponseIsNotOkReturn(car.getLeft());
                    }
                    else return serverResponseIsNotOkReturn(it.getLeft());
                });
    }

    public Mono<ServerResponse> updateCar(final ServerRequest serverRequest) {
        System.out.println("update car handler called");
        return serverRequest
                .bodyToMono(CarDTO.class)
                .map(CarDTO::toCar)
                .flatMap(it -> {
                    if (it.isRight()) {
                        final var car = carService.updateCar(it.get());
                        if (car.isRight()){
                            System.out.println("car.get() = " + car.get());
                            return serverResponseIsOkReturn(new CarDTO(car.get()));   
                        }
                        else return serverResponseIsNotOkReturn(car.getLeft());
                    }
                    else return serverResponseIsNotOkReturn(it.getLeft());
                });
    }

    public Mono<ServerResponse> deleteCar(final ServerRequest serverRequest) {
        System.out.println("delete car handler called");
        return serverRequest
                .bodyToMono(CarDTO.class)
                .map(CarDTO::toCar)
                .flatMap(it -> {
                    if (it.isRight()) {
                        final var car = carService.deleteCar(it.get());
                        if (car.isPresent()){
                            System.out.println("car.get() = " + car.get());
                            return serverResponseIsOkReturn(new CarDTO(car.get()));
                        }
                        else return serverResponseIsNotOkReturn(car);
                    }
                    else return serverResponseIsNotOkReturn(it.getLeft());
                });
    }

    public Mono<ServerResponse> deleteAllCars(final ServerRequest serverRequest) {
        System.out.println("delete all car handler called");
        return returnCollectionOfCarsByCriteria(carService.deleteAllCars());
    }

    public Mono<ServerResponse> deleteCarByPlatNumber(final ServerRequest serverRequest) {
        System.out.println("delete car by plate number handler called");
        return returnCarInOptional(carService
                .deleteCarByPlatNumber(serverRequest.pathVariable("plateNumber")
                ));
    }

    private Mono<ServerResponse> returnCarInOptional(final Optional<Car> resultCar) {
        return resultCar
                .map(this::returnCarOfCarsByCriteria)
                .orElseGet(() -> serverResponseIsNotOkReturn(resultCar));
    }

    private Mono<ServerResponse> returnCollectionOfCarsByCriteria(final Collection<Car> cars) {
        return serverResponseIsOkReturn(cars
                .stream()
                .peek(System.out::println)
                .map(CarDTO::new)
                .toList());
    }

    private Mono<ServerResponse> returnCarOfCarsByCriteria(final Car car) {
        return serverResponseIsOkReturn(new CarDTO(car));
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
