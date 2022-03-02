package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.framework.car.service;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.model.Car;
import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.repository.CarRepository;
import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.service.CarService;
import io.vavr.control.Either;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@SpringBootTest
class CarServiceTest {

    final CarService carServiceInstance;

    CarServiceTest(@Autowired final CarService carServiceInstance) {
        this.carServiceInstance = carServiceInstance;
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
    @DisplayName("saveCar: save null car must not be valid")
    void save_null_car_must_not_be_valid() {
        final Car car = null;
        final var result =
                carServiceInstance.saveCar(car)
                        .getLeft();
        Assertions.assertTrue(result instanceof CarService.ServiceCarError.CarWithNullParameterError);
    }

    @Test
    @DisplayName("saveCar: valid car must be saved in repository with one instance")
    void valid_car_must_be_saved_in_repository_with_one_instance() {
        final var car =
                Car.of(
                                "222TN2222",
                                "BMW",
                                LocalDate.of(2020, 10, 10)
                        )
                        .get();
        final var result =
                carServiceInstance.saveCar(car)
                        .get();
        final var totalCarsAfterAddingCar =
                carServiceInstance
                        .getAllCars()
                        .size();
        Assertions.assertAll(
                () -> Assertions.assertEquals(car, result),
                () -> Assertions.assertEquals(1, totalCarsAfterAddingCar)
        );
    }

    @Test
    @DisplayName("saveCar: duplicated car must not be saved")
    void duplicated_car_must_not_be_saved() {
        final var car =
                Car.of(
                                "222TN2222",
                                "BMW",
                                LocalDate.of(2020, 10, 10)
                        )
                        .get();
        carServiceInstance.saveCar(car);
        final var result =
                carServiceInstance.saveCar(car)
                        .getLeft();
        Assertions.assertTrue(result instanceof CarService.ServiceCarError.CarWithPlateNumberAlreadyExistError);
    }

    @Test
    @DisplayName("saveCar: saved two cars with the same plat number with the expectation to fail with null parameters")
    void saved_two_cars_with_the_same_plat_number_with_the_expectation_to_fail_with_null_parameters() {
        final var car1 =
                Car.of(
                                "222TN2222",
                                "BMW",
                                LocalDate.of(2020, 10, 10)
                        )
                        .get();
        final var car2 =
                Car.of(
                                car1.getPlatNumber(),
                                "Mercedes",
                                LocalDate.of(2019, 12, 12)
                        )
                        .get();
        carServiceInstance.saveCar(car1);
        final var result =
                carServiceInstance.saveCar(car2)
                        .getLeft();
        Assertions.assertTrue(result instanceof CarService.ServiceCarError.CarWithPlateNumberAlreadyExistError);
    }

    @Test
    @DisplayName("getAllCars: no car will be return with no cars in repository")
    void no_car_will_be_return_with_no_cars_in_repository() {
        final var result = carServiceInstance.getAllCars().isEmpty();
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("getAllCars: only one car should return with one car saved")
    void only_one_car_should_return_with_one_car_saved() {
        final var car =
                Car.of(
                                "222TN2222",
                                "BMW",
                                LocalDate.of(2020, 10, 10)
                        )
                        .get();
        carServiceInstance.saveCar(car);
        final var result = carServiceInstance.getAllCars();

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, result.size()),
                () -> Assertions.assertTrue(result.contains(car))
        );
    }

    @Test
    @DisplayName("getAllCars: only two cars should return with only two cars saved")
    void only_two_cars_should_return_with_only_two_cars_saved() {
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
        final var result = carServiceInstance.getAllCars().size();
        Assertions.assertEquals(size, result);
    }

    @Test
    @DisplayName("getByPlateNumber: empty should return if a car with a plate number that not exist")
    void empty_should_return_if_a_car_with_a_plate_number_that_not_exist() {
        final var plateNumber = "202TN2022";
        final var result = carServiceInstance
                .getCarByPlatNumber(plateNumber);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getByPlateNumber: a single unique car should return if a single car have that plate number")
    void a_single_unique_car_should_return_if_a_single_car_have_that_plate_number() {
        final var car =
                Car.of(
                                "222TN2222",
                                "BMW",
                                LocalDate.of(2020, 10, 10)
                        )
                        .get();
        carServiceInstance.saveCar(car);
        final var result = carServiceInstance
                .getCarByPlatNumber(car.getPlatNumber());
        Assertions.assertAll(
                () -> Assertions.assertTrue(result.isPresent()),
                () -> Assertions.assertEquals(car, result.get())
        );
    }

    @Test
    @DisplayName("getByPlateNumber: a single unique car should return if multiple cars exist and a car with a plate number exist")
    void a_single_unique_car_should_return_if_multiple_cars_exist_and_a_car_with_a_plate_number_exist() {
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
        final var car =
                Car.of(
                                "222TN2222",
                                "BMW",
                                LocalDate.of(2020, 10, 10)
                        )
                        .get();
        carServiceInstance.saveCar(car);
        final var result = carServiceInstance
                .getCarByPlatNumber(car.getPlatNumber()).get();
        Assertions.assertAll(
                () -> Assertions.assertEquals(car, result),
                () -> Assertions.assertTrue(result instanceof Car)
        );
    }

    @Test
    @DisplayName("getByType: zero size collection should return if repository dont have with received type")
    void zero_size_collection_should_return_if_repository_is_empty_with_received_type() {
        final var type = "BMW";
        final var result = carServiceInstance
                .getAllCarsByType(type);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    @DisplayName("getByType: one car should return if the repository have one car with the received type")
    void one_car_should_return_if_the_repository_have_one_car_with_the_received_type() {
        final var type = "BMW";
        final var car =
                Car.of(
                                "222TN2222",
                                type,
                                LocalDate.of(2020, 10, 10)
                        )
                        .get();
        carServiceInstance.saveCar(car);
        final var result = carServiceInstance
                .getAllCarsByType(type);
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, result.size()),
                () -> Assertions.assertTrue(result
                        .stream()
                        .anyMatch(c -> c.getType().equals(type)))
        );
    }

    @Test
    @DisplayName("getByType: one car should return if the repository have many cars and one car with the received type")
    void one_car_should_return_if_the_repository_have_many_cars_and_one_car_with_the_received_type() {
        final var type = "BMW";
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
        final var car =
                Car.of(
                                "222TN2222",
                                type,
                                LocalDate.of(2020, 10, 10)
                        )
                        .get();
        carServiceInstance.saveCar(car);
        final var result = carServiceInstance
                .getAllCarsByType(type);
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, result.size()),
                () -> Assertions.assertTrue(result.stream().anyMatch(c -> c.getType().equals(type)))
        );
    }

    @Test
    @DisplayName("getByType: five cars should return if the repository have five car with the received type")
    void five_cars_should_return_if_the_repository_have_five_car_with_the_received_type() {
        final var type = "BMW";
        final var size = 5;
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
        final var result = carServiceInstance
                .getAllCarsByType(type);
        Assertions.assertAll(
                () -> Assertions.assertEquals(size, result.size()),
                () -> Assertions.assertTrue(result.stream().allMatch(c -> c.getType().equals(type)))
        );
    }

    @Test
    @DisplayName("getByReleaseYear: zero size collection should return if repository dont have received release year")
    void zero_size_collection_should_return_if_repository_is_empty_with_received_release_year() {
        final var releaseYear = 2020;
        final var result = carServiceInstance
                .getAllCarsByReleaseYear(releaseYear);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    @DisplayName("getByReleaseYear: one car should return if the repository have many cars and one car with the received release year")
    void one_car_should_return_if_the_repository_have_many_cars_and_one_car_with_the_received_release_year() {
        final var releaseYearThisYear = 2020;
        final var size = 5;
        IntStream.iterate(1, i -> i + 1)
                .limit(size)
                .boxed()
                .map(it ->
                        Car.of(
                                UUID.randomUUID().toString(),
                                UUID.randomUUID().toString(),
                                generateRandomLocalDateMinusTenYear()
                        )
                )
                .map(Either::get)
                .forEach(carServiceInstance::saveCar);
        final var car =
                Car.of(
                                "222TN2222",
                                "BMW",
                                LocalDate.of(releaseYearThisYear, 10, 10)
                        )
                        .get();
        carServiceInstance.saveCar(car);
        final var result = carServiceInstance
                .getAllCarsByReleaseYear(releaseYearThisYear);
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, result.size()),
                () -> Assertions.assertTrue(result
                        .stream()
                        .map(Car::getReleaseDate)
                        .findFirst()
                        .isPresent()
                ),
                () -> Assertions.assertTrue(
                        result
                                .stream()
                                .allMatch(c -> c.getReleaseDate().getYear() == releaseYearThisYear)
                )
        );
    }

    @Test
    @DisplayName("getByReleaseYear: five car should return if the repository only have five cars with the received release year")
    void five_car_should_return_if_the_repository_have_only_five_cars_with_the_received_release_year_in_find_by_release_year() {
        final var releaseYearThisYear = 2020;
        final var size = 5;
        IntStream.iterate(1, i -> i + 1)
                .limit(size)
                .boxed()
                .map(it ->
                        Car.of(
                                UUID.randomUUID().toString(),
                                UUID.randomUUID().toString(),
                                LocalDate.of(releaseYearThisYear, 10, 10)
                        )
                )
                .map(Either::get)
                .forEach(carServiceInstance::saveCar);
        final var result = carServiceInstance
                .getAllCarsByReleaseYear(releaseYearThisYear);
        Assertions.assertAll(
                () -> Assertions.assertEquals(size, result.size()),
                () -> Assertions.assertTrue(result
                        .stream()
                        .map(Car::getReleaseDate)
                        .findFirst()
                        .isPresent()
                ),
                () -> Assertions.assertTrue(
                        result
                                .stream()
                                .allMatch(c -> c.getReleaseDate().getYear() == releaseYearThisYear)
                )
        );
    }

    @Test
    @DisplayName("getByReleaseYear: five car should return if the repository have five cars with the received release year in find by release date")
    void five_car_should_return_if_the_repository_have_five_cars_with_the_received_release_year_in_find_by_release_year() {
        final var releaseYearThisYear = 2020;
        final var size = 5;
        IntStream.iterate(1, i -> i + 1)
                .limit(size)
                .boxed()
                .map(it ->
                        Car.of(
                                UUID.randomUUID().toString(),
                                UUID.randomUUID().toString(),
                                LocalDate.of(releaseYearThisYear, 10, 10)
                        )
                )
                .map(Either::get)
                .forEach(carServiceInstance::saveCar);
        IntStream.iterate(1, i -> i + 1)
                .limit(size)
                .boxed()
                .map(it ->
                        Car.of(
                                UUID.randomUUID().toString(),
                                UUID.randomUUID().toString(),
                                generateRandomLocalDateMinusTenYear()
                        )
                )
                .map(Either::get)
                .forEach(carServiceInstance::saveCar);
        final var result = carServiceInstance
                .getAllCarsByReleaseYear(releaseYearThisYear);
        Assertions.assertAll(
                () -> Assertions.assertEquals(size, result.size()),
                () -> Assertions.assertTrue(result
                        .stream()
                        .map(Car::getReleaseDate)
                        .findFirst()
                        .isPresent()
                ),
                () -> Assertions.assertTrue(
                        result
                                .stream()
                                .allMatch(c -> c.getReleaseDate().getYear() == releaseYearThisYear)
                )
        );
    }

    @Test
    @DisplayName("update: error should be returned if plate number dont exist in update")
    void error_should_be_returned_if_plate_number_dont_exist_in_update() {
        final var car =
                Car.of(
                                "222TN2222",
                                "BMW",
                                LocalDate.of(2020, 10, 10)
                        )
                        .get();
        final var result = carServiceInstance.updateCar(car).getLeft();
        Assertions.assertTrue(result instanceof CarService.ServiceCarError.CarWithPlateNumberNotExistError);
    }

    @Test
    @DisplayName("update: error should be returned if car is null in update")
    void error_should_be_returned_if_car_is_null_in_update() {
        final Car car = null;
        final var result = carServiceInstance.updateCar(car).getLeft();
        Assertions.assertTrue(result instanceof CarService.ServiceCarError.CarWithNullParameterError);
    }

    @Test
    @DisplayName("update: one car should be returned if only one car with same plate number exit in repository in update")
    void one_car_should_be_returned_if_only_one_car_with_same_plate_number_exit_in_repository_in_update() {
        final var car1 =
                Car.of(
                                "222TN2222",
                                "BMW",
                                generateRandomLocalDateMinusTenYear()
                        )
                        .get();
        carServiceInstance.saveCar(car1);
        final var car2 =
                Car.of(
                                "222TN2222",
                                "Mercedes",
                                generateRandomLocalDateMinusTenYear()
                        )
                        .get();
        final var result = carServiceInstance.updateCar(car2).get();
        Assertions.assertEquals(result, car2);
    }

    @Test
    @DisplayName("update: one car should be returned if only one car with same plate number exit in full repository_in_update")
    void one_car_should_be_returned_if_only_one_car_with_same_plate_number_exit_in_full_repository_in_update() {
        final var size = 5;
        IntStream.iterate(1, i -> i + 1)
                .limit(size)
                .boxed()
                .map(it ->
                        Car.of(
                                UUID.randomUUID().toString(),
                                UUID.randomUUID().toString(),
                                generateRandomLocalDateMinusTenYear()
                        )
                )
                .map(Either::get)
                .forEach(carServiceInstance::saveCar);
        final var car1 =
                Car.of(
                                "222TN2222",
                                "BMW",
                                generateRandomLocalDateMinusTenYear()
                        )
                        .get();
        carServiceInstance.saveCar(car1);
        final var car2 =
                Car.of(
                                "222TN2222",
                                "Mercedes",
                                generateRandomLocalDateMinusTenYear()
                        )
                        .get();
        final var result = carServiceInstance.updateCar(car2).get();
        Assertions.assertEquals(result, car2);
    }

    @Test
    @DisplayName("delete: empty should be returned if car is null")
    void error_should_be_returned_if_car_is_null_in_delete_car() {
        final Car car = null;
        final var result = carServiceInstance.deleteCar(car);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("delete: empty should be returned if plate number dont exist")
    void error_should_be_returned_if_plate_number_dont_exist() {
        final var car =
                Car.of(
                                "222TN2222",
                                UUID.randomUUID().toString(),
                                generateRandomLocalDateMinusTenYear()
                        )
                        .get();
        final var result = carServiceInstance.deleteCar(car);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("delete: one car should be returned if only one car with the same plate number exist")
    void one_car_should_be_returned_if_only_one_car_with_the_same_plate_number_exist() {
        final var car =
                Car.of(
                                "222TN2222",
                                UUID.randomUUID().toString(),
                                generateRandomLocalDateMinusTenYear()
                        )
                        .get();
        carServiceInstance.saveCar(car);
        final var result = carServiceInstance.deleteCar(car);
        Assertions.assertEquals(car,result.get());
    }

    @Test
    @DisplayName("delete: one car should be returned if plate number exist in full repository")
    void one_car_should_be_returned_if_plate_number_exist_in_full_repository() {
        final var size = 5;
        IntStream.iterate(1, i -> i + 1)
                .limit(size)
                .boxed()
                .map(it ->
                        Car.of(
                                UUID.randomUUID().toString(),
                                UUID.randomUUID().toString(),
                                generateRandomLocalDateMinusTenYear()
                        )
                )
                .map(Either::get)
                .forEach(carServiceInstance::saveCar);
        final var car =
                Car.of(
                                "222TN2222",
                                UUID.randomUUID().toString(),
                                generateRandomLocalDateMinusTenYear()
                        )
                        .get();
        carServiceInstance.saveCar(car);
        final var result = carServiceInstance.deleteCar(car);
        Assertions.assertEquals(car,result.get());
    }

    @Test
    @DisplayName("delete: error return when car has null plate number in delete operation")
    void error_return_when_car_has_null_plate_number_in_delete_operation() {
        final String plateNumber =null;
        final var result = carServiceInstance.deleteCarByPlatNumber(plateNumber).isEmpty();
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("delete: delete all cars should return empty when the car service return empty list")
    void delete_all_cars_should_return_empty_when_the_car_service_return_empty_list() {
        final var result = carServiceInstance.deleteAllCars().size();
        Assertions.assertEquals(0,result);
    }

    @Test
    @DisplayName("delete: delete all cars should return empty if the car service is full")
    void delete_all_cars_should_return_empty_if_the_repository_is_full() {
        final var size = 5;
        IntStream.iterate(1, i -> i + 1)
                .limit(size)
                .boxed()
                .map(it ->
                        Car.of(
                                UUID.randomUUID().toString(),
                                UUID.randomUUID().toString(),
                                generateRandomLocalDateMinusTenYear()
                        )
                )
                .map(Either::get)
                .forEach(carServiceInstance::saveCar);
        final var result = carServiceInstance.deleteAllCars().size();
        Assertions.assertEquals(0,result);
    }

    private LocalDate generateRandomLocalDateMinusTenYear() {
        final var minDay = LocalDate.of(1970, 1, 1).toEpochDay();
        final var maxDay = LocalDate.now().minusYears(10).toEpochDay();
        final var randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDate.ofEpochDay(randomDay);
    }
}