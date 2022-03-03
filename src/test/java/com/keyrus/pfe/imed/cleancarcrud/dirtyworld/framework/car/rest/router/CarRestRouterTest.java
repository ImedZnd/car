package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.framework.car.rest.router;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.model.Car;
import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.service.CarService;
import com.keyrus.pfe.imed.cleancarcrud.dirtyworld.dto.CarDTO;
import com.keyrus.pfe.imed.cleancarcrud.dirtyworld.model.Date;
import io.vavr.control.Either;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.IntStream;

@WebFluxTest
class CarRestRouterTest {

    final CarService carServiceInstance;

    private final WebTestClient webTestClient;

    CarRestRouterTest(@Autowired CarService carServiceInstance, @Autowired WebTestClient webTestClient) {
        this.carServiceInstance = carServiceInstance;
        this.webTestClient = webTestClient;
    }

    @BeforeEach
    void beforeEach() {
        carServiceInstance.deleteAllCars();
    }

    @AfterEach
    void afterEach() {
        carServiceInstance.deleteAllCars();
    }

    @Test
    @DisplayName("get all cars should return empty with empty repository")
    void get_all_cars_should_return_empty_with_empty_repository() {
        webTestClient
                .get()
                .uri("/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(CarDTO.class)
                .hasSize(0);
    }

    @Test
    @DisplayName("get all cars should return five cars with five cars in repository")
    void get_all_cars_should_return_five_cars_with_five_cars_in_repository() {
        final var size = 5;
        IntStream.iterate(1, i -> i + 1)
                .limit(size)
                .boxed()
                .map(it ->
                        Car.of(
                                UUID.randomUUID().toString(),
                                UUID.randomUUID().toString(),
                                LocalDate.of(2020, 10, 10)
                        )
                )
                .map(Either::get)
                .forEach(carServiceInstance::saveCar);
        webTestClient
                .get()
                .uri("/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(CarDTO.class)
                .hasSize(size);
    }

    @Test
    @DisplayName("only one car should return with one car saved")
    void only_one_car_should_return_with_one_car_saved() {
        final var car =
                Car.of(
                                "222TN2222",
                                "BMW",
                                LocalDate.of(2020, 10, 10)
                        )
                        .get();
        carServiceInstance.saveCar(car);
        webTestClient
                .get()
                .uri("/")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(CarDTO.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("only one car should return with one car saved with provided plate number")
    void only_one_car_should_return_with_one_car_saved_with_provided_plate_number() {
        final var plateNumber = "222TN2222";
        final var car =
                Car.of(
                                plateNumber,
                                "BMW",
                                LocalDate.of(2020, 10, 10)
                        )
                        .get();
        carServiceInstance.saveCar(car);
        webTestClient
                .get()
                .uri("/plateNumber/" + plateNumber)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(CarDTO.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("error will return when car with provided plat number not exist")
    void error_will_return_when_car_with_provided_plat_number_not_exist() {
        webTestClient
                .get()
                .uri("/plateNumber/222TN2222")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("one car will return when car with one car exist in the repo with provided type")
    void one_car_will_return_when_car_with_one_car_exist_in_the_repo_with_provided_type() {
        final var car =
                Car.of(
                                "222TN2222",
                                "BMW",
                                LocalDate.of(2020, 10, 10)
                        )
                        .get();
        carServiceInstance.saveCar(car);
        final var size = 5;
        IntStream.iterate(1, i -> i + 1)
                .limit(size)
                .boxed()
                .map(it ->
                        Car.of(
                                UUID.randomUUID().toString(),
                                UUID.randomUUID().toString(),
                                LocalDate.of(2020, 10, 10)
                        )
                )
                .map(Either::get)
                .forEach(carServiceInstance::saveCar);
        webTestClient
                .get()
                .uri("/type/BMW")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(CarDTO.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("five car will return when car with one car exist in the repo with provided type")
    void five_car_will_return_when_car_with_one_car_exist_in_the_repo_with_provided_type() {
        final var size = 5;
        final var type = "BMW";
        IntStream.iterate(1, i -> i + 1)
                .limit(size)
                .boxed()
                .map(it ->
                        Car.of(
                                UUID.randomUUID().toString(),
                                type,
                                LocalDate.of(2020, 10, 10)
                        )
                )
                .map(Either::get)
                .forEach(carServiceInstance::saveCar);
        webTestClient
                .get()
                .uri("/type/" + type)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(CarDTO.class)
                .hasSize(5);
    }

    @Test
    @DisplayName("five car will return when car with five car exist in the repo with provided release year")
    void five_car_will_return_when_car_with_five_car_exist_in_the_repo_with_provided_release_year() {
        final var size = 5;
        final var releaseYear = 2010;
        IntStream.iterate(1, i -> i + 1)
                .limit(size)
                .boxed()
                .map(it ->
                        Car.of(
                                UUID.randomUUID().toString(),
                                UUID.randomUUID().toString(),
                                LocalDate.of(releaseYear, 10, 10)
                        )
                )
                .map(Either::get)
                .forEach(carServiceInstance::saveCar);
        webTestClient
                .get()
                .uri("/releaseYear/" + releaseYear)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(CarDTO.class)
                .hasSize(5);
    }

    @Test
    @DisplayName("no car will return when car with no car exist in the repo with provided release year")
    void no_will_return_when_car_with_no_car_exist_in_the_repo_with_provided_release_year() {
        final var size = 5;
        final var releaseYear = 2010;
        IntStream.iterate(1, i -> i + 1)
                .limit(size)
                .boxed()
                .map(it ->
                        Car.of(
                                UUID.randomUUID().toString(),
                                UUID.randomUUID().toString(),
                                LocalDate.of(2011, 10, 10)
                        )
                )
                .map(Either::get)
                .forEach(carServiceInstance::saveCar);
        webTestClient
                .get()
                .uri("/releaseYear/" + releaseYear)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(CarDTO.class)
                .hasSize(0);
    }

    @Test
    @DisplayName("one car return on save operation with no car in repo")
    void one_car_return_on_save_operation_with_no_car_in_repo() {
        final var carDTO =
                new CarDTO(
                        "xxTNxxx",
                        "xxx",
                        new Date(10, 10, 2020)
                );
        webTestClient
                .post()
                .uri("/save")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(carDTO))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(CarDTO.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("error return on save operation with bad parameters")
    void error_return_on_save_operation_with_bad_parameters_car() {
        final CarDTO carDTO =
                new CarDTO(
                        "xxTNxxx",
                        "xxx",
                        new Date(10, 10, 202222220)
                );
        webTestClient
                .post()
                .uri("/save")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(carDTO))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
    @Test
    @DisplayName("error return on save operation with bad year")
    void error_return_on_save_operation_with_bad_year() {
        final CarDTO carDTO =
                new CarDTO(
                        "xxTNxxx",
                        "xxx",
                        new Date(10, 55, 2020)
                );
        webTestClient
                .post()
                .uri("/save")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(carDTO))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("error return on save operation with car in repo with same plate number")
    void error_return_on_save_operation_with_car_in_repo_with_same_plate_number() {
        final var plateNumber = "222TN2222";
        final var carDTO =
                new CarDTO(
                        plateNumber,
                        "BMW",
                        new Date(10, 10, 2020)
                );
        final var car =
                Car.of(
                                plateNumber,
                                "BMW",
                                LocalDate.of(2020, 10, 10)
                        )
                        .get();
        carServiceInstance.saveCar(car);
        webTestClient
                .post()
                .uri("/save")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(carDTO))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("five cars return on delete operation with five cars in repo")
    void five_cars_return_on_delete_operation_with_five_cars_in_repo() {
        final var size = 5;
        IntStream.iterate(1, i -> i + 1)
                .limit(size)
                .boxed()
                .map(it ->
                        Car.of(
                                UUID.randomUUID().toString(),
                                UUID.randomUUID().toString(),
                                LocalDate.of(2011, 10, 10)
                        )
                )
                .map(Either::get)
                .forEach(carServiceInstance::saveCar);
        webTestClient
                .delete()
                .uri("/deleteAll")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(CarDTO.class)
                .hasSize(size);
    }

    @Test
    @DisplayName("error return in update car not exist")
    void error_return_in_update_car_not_exist() {
        final var plateNumber = "222TN2222";
        final var carDTO =
                new CarDTO(
                        plateNumber,
                        "BMW",
                        new Date(10, 10, 2020)
                );
        final var car =
                Car.of(
                                plateNumber,
                                "xxxx",
                                LocalDate.of(2020, 10, 10)
                        )
                        .get();
        carServiceInstance.saveCar(car);
        webTestClient
                .put()
                .uri("/update")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(carDTO))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(CarDTO.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("error return in delete car not exist")
    void error_return_in_delete_car_not_exist() {
        final var plateNumber = "222TN2222";
        final var carDTO =
                new CarDTO(
                        plateNumber,
                        "BMW",
                        new Date(10, 10, 2020)
                );
        webTestClient
                .post()
                .uri("/delete")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(carDTO))
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBodyList(CarDTO.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("car return in delete car exist")
    void car_return_in_delete_car_exist() {
        final var plateNumber = "222TN2222";
        final var carDTO =
                new CarDTO(
                        plateNumber,
                        "BMW",
                        new Date(10, 10, 2020)
                );
        final var car =
                Car.of(
                                plateNumber,
                                "xxxx",
                                LocalDate.of(2020, 10, 10)
                        )
                        .get();
        carServiceInstance.saveCar(car);
        webTestClient
                .post()
                .uri("/delete")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(carDTO))
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBodyList(CarDTO.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("non car return in delete all car with empty repo")
    void non_car_return_in_delete_all_car_with_empty_repo() {
        webTestClient
                .delete()
                .uri("/deleteAll")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(CarDTO.class)
                .hasSize(0);
    }

    @Test
    @DisplayName("five car return in delete all car with five cars in repo")
    void fave_car_return_in_delete_all_car_with_five_cars_in_repo() {
        final var size = 5;
        IntStream.iterate(1, i -> i + 1)
                .limit(size)
                .boxed()
                .map(it ->
                        Car.of(
                                UUID.randomUUID().toString(),
                                UUID.randomUUID().toString(),
                                LocalDate.of(2011, 10, 10)
                        )
                )
                .map(Either::get)
                .forEach(carServiceInstance::saveCar);
        webTestClient
                .delete()
                .uri("/deleteAll")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(CarDTO.class)
                .hasSize(5);
    }
}