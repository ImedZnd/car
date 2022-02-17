package com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    @Test
    @DisplayName("car should be initiated with initial fields")
    void car_should_be_initiated_with_initial_fields() {
        final var platNumber = "120TN5321";
        final var type = "BMW";
        final var releaseDate = LocalDate.of(2022, 1, 5);
        final var result =
                Car.of(
                                platNumber,
                                type,
                                releaseDate
                        )
                        .get();

        assertAll(
                () -> assertEquals(platNumber, result.getPlatNumber()),
                () -> assertEquals(type, result.getType()),
                () -> assertEquals(releaseDate, result.getReleaseDate())
        );

    }

    @Test
    @DisplayName("car plate number must be initiated not empty")
    void car_plate_number_must_be_initiated_not_empty() {
        final var platNumber = "";
        final var type = "Opel";
        final var releaseDate = LocalDate.of(2020, 1, 5);
        final var result =
                Car.of(
                                platNumber,
                                type,
                                releaseDate
                        )
                        .getLeft();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertTrue(result.stream().allMatch(it -> it instanceof Car.CarError.PlateNumberError))
        );
    }

    @Test
    @DisplayName("car type must be initiated not empty")
    void car_type_must_be_initiated_not_empty() {
        final var platNumber = "200TN5120";
        final var type = "";
        final var releaseDate = LocalDate.of(2020, 1, 5);
        final var result =
                Car.of(
                                platNumber,
                                type,
                                releaseDate
                        )
                        .getLeft();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertTrue(result.stream().allMatch(it -> it instanceof Car.CarError.TypeError))
        );
    }

    @Test
    @DisplayName("car release date must be initiated not empty")
    void car_release_dat_must_be_initiated_not_empty() {
        final var platNumber = "200TN5120";
        final var type = "BMW";
        final var releaseDate = LocalDate.of(2025, 1, 5);
        final var result =
                Car.of(
                                platNumber,
                                type,
                                releaseDate
                        )
                        .getLeft();

        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertTrue(result.stream().allMatch(it -> it instanceof Car.CarError.ReleaseDateError))
        );
    }

    @Test
    @DisplayName("car have all param not valid")
    void car_have_all_param_not_valid() {
        final var platNumber = "";
        final var type = "";
        final var releaseDate = LocalDate.of(2025, 1, 5);
        final var result =
                Car.of(
                                platNumber,
                                type,
                                releaseDate
                        )
                        .getLeft();

        assertAll(
                () -> assertEquals(3, result.size()),
                () -> assertTrue(result.stream().anyMatch(it -> it instanceof Car.CarError.PlateNumberError)),
                () -> assertTrue(result.stream().anyMatch(it -> it instanceof Car.CarError.TypeError)),
                () -> assertTrue(result.stream().anyMatch(it -> it instanceof Car.CarError.ReleaseDateError))
        );
    }
}