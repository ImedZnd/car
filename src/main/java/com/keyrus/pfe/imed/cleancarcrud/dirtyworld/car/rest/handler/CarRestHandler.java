package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.rest.handler;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.model.Car;
import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.service.CarService;
import com.keyrus.pfe.imed.cleancarcrud.dirtyworld.dto.CarDTO;
import io.vavr.control.Either;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;

public final class CarRestHandler {

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

    public Mono<ServerResponse> getAllCars() {
        return
                bodyValueToOkServerResponse(
                        carsToCarsDTO(
                                carService.getAllCars()
                        )
                );
    }

    public Mono<ServerResponse> getCarByPlatNumber(final ServerRequest serverRequest) {
        return
                optionalToOkServerResponseOrNotFoundServerResponse(
                        carService.getCarByPlatNumber(
                                serverRequest.pathVariable("plateNumber")
                        )
                );
    }

    public Mono<ServerResponse> getAllCarsByType(final ServerRequest serverRequest) {
        return
                bodyValueToOkServerResponse(
                        carsToCarsDTO(
                                carService.getAllCarsByType(
                                        serverRequest.pathVariable("type")
                                )
                        )
                );
    }

    public Mono<ServerResponse> getAllCarsByReleaseYear(final ServerRequest serverRequest) {
        final var releaseYear =
                Integer.parseInt(
                        serverRequest.pathVariable("releaseYear")
                );
        return
                bodyValueToOkServerResponse(
                        carsToCarsDTO(
                                carService.getAllCarsByReleaseYear(releaseYear)
                        )
                );
    }

    public Mono<ServerResponse> saveCar(final ServerRequest serverRequest) {
        return
                serverRequest
                        .bodyToMono(CarDTO.class)
                        .map(CarDTO::toCar)
                        .flatMap(carErrorsOrCar -> carServiceOperationWithEitherCarServiceErrorOrCarAsResultToServerResponse(carErrorsOrCar, CarService::saveCar));
    }

    public Mono<ServerResponse> updateCar(final ServerRequest serverRequest) {
        return
                serverRequest
                        .bodyToMono(CarDTO.class)
                        .map(CarDTO::toCar)
                        .flatMap(carErrorsOrCar -> carServiceOperationWithEitherCarServiceErrorOrCarAsResultToServerResponse(carErrorsOrCar, CarService::updateCar));
    }

    public Mono<ServerResponse> deleteCar(final ServerRequest serverRequest) {
        return
                serverRequest
                        .bodyToMono(CarDTO.class)
                        .map(CarDTO::getPlatNumber)
                        .map(carService::deleteCar)
                        .flatMap(this::optionalToOkServerResponseOrNotFoundServerResponse);
    }

    public Mono<ServerResponse> deleteAllCars() {
        return
                bodyValueToOkServerResponse(
                        carsToCarsDTO(
                                carService.deleteAllCars()
                        )
                );
    }

    public Mono<ServerResponse> deleteCarByPlatNumber(final ServerRequest serverRequest) {
        return
                optionalToOkServerResponseOrNotFoundServerResponse(
                        carService.deleteCar(
                                serverRequest.pathVariable("plateNumber")
                        )
                );
    }

    private Mono<ServerResponse> optionalToOkServerResponseOrNotFoundServerResponse(final Optional<Car> optional) {
        return
                optional
                        .map(CarDTO::new)
                        .map(ServerResponse.ok()::bodyValue)
                        .orElse(ServerResponse.notFound().build());
    }

    private <T> Mono<ServerResponse> bodyValueToOkServerResponse(
            final T validResponseBodyValue
    ) {
        return ServerResponse
                .ok()
                .bodyValue(
                        validResponseBodyValue
                );
    }

    private Collection<CarDTO> carsToCarsDTO(
            final Collection<Car> cars
    ) {
        return
                cars
                        .stream()
                        .map(CarDTO::new)
                        .toList();
    }

    private Mono<ServerResponse> carServiceErrorToBadRequestServerResponseWithErrorHeader(
            final CarService.ServiceCarError carServiceError
    ) {
        return
                ServerResponse
                        .badRequest()
                        .header(
                                "error",
                                carServiceError.message()
                        )
                        .build();
    }

    private Either<Mono<ServerResponse>, Mono<ServerResponse>> eitherCarServiceErrorOrCarToServerResponse(
            final Either<? extends CarService.ServiceCarError, Car> carServiceErrorOrCar
    ) {
        return
                carServiceErrorOrCar
                        .map(CarDTO::new)
                        .map(this::bodyValueToOkServerResponse)
                        .mapLeft(this::carServiceErrorToBadRequestServerResponseWithErrorHeader);
    }

    private Mono<ServerResponse> carServiceOperationWithEitherCarServiceErrorOrCarAsResultToServerResponse(
            final Either<Collection<? extends Car.CarError>, Car> carErrorsOrCar,
            final BiFunction<CarService, Car, Either<? extends CarService.ServiceCarError, Car>> applyOnCar
    ) {
        return
                carErrorsOrCar
                        .map(car ->
                                applyOnCar
                                        .apply(
                                                carService,
                                                car
                                        )
                        )
                        .mapLeft(this::carErrorsToBadRequestServerResponse)
                        .flatMap(this::eitherCarServiceErrorOrCarToServerResponse)
                        .fold(
                                it -> it,
                                it -> it
                        );
    }

    private Mono<ServerResponse> carErrorsToBadRequestServerResponse(
            final Collection<? extends Car.CarError> carErrors
    ) {
        return
                ServerResponse
                        .badRequest()
                        .headers(
                                header ->
                                        addCarErrorsToResponseErrorHeader(
                                                header,
                                                carErrors
                                        )
                        )
                        .build();
    }

    private void addCarErrorsToResponseErrorHeader(
            final HttpHeaders headers,
            final Collection<? extends Car.CarError> carErrors
    ) {
        carErrors.forEach(
                carError ->
                        addCarErrorToResponseErrorHeader(
                                headers,
                                carError
                        )
        );
    }

    private void addCarErrorToResponseErrorHeader(
            final HttpHeaders headers,
            final Car.CarError carError
    ) {
        headers.add(
                "error",
                carError.message()
        );
    }

}
