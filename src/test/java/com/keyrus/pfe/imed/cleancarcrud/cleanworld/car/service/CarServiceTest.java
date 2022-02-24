package com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.service;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.repository.CarRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
record CarServiceTest(CarRepository carRepository) {

    @Test
    @DisplayName("car service singleton must create one instance")
    void car_service_singleton_must_create_one_instance() {
        final var serviceCar1 = CarService.getInstance(carRepository);
        final var serviceCar2 = CarService.getInstance(carRepository);
        System.out.println("serviceCar2 = " + serviceCar2);
        System.out.println("serviceCar1 = " + serviceCar1);
        assertEquals(serviceCar1, serviceCar2);
    }

    @Test
    @DisplayName("car service singleton must create one instance with second test")
    void car_service_singleton_must_create_one_instance_with_second_test() {
        final var serviceCar1 = CarService.getInstance(carRepository);
        final var serviceCar2 = CarService.getInstance(carRepository);
        System.out.println("serviceCar2 = " + serviceCar2);
        System.out.println("serviceCar1 = " + serviceCar1);
        assertEquals(serviceCar1, serviceCar2);
    }

    @Test
    @DisplayName("car service singleton must create one instance with second test")
    void x() {
        final var serviceCar1 = CarService.getInstance(carRepository);
        serviceCar1.getAllCars();
    }
}
