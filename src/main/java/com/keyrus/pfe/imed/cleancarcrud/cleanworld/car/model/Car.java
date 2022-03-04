package com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.model;

import io.vavr.control.Either;
import lombok.Data;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public final class Car {

    private final String platNumber;
    private final String type;
    private final LocalDate releaseDate;

    private Car(
            final String platNumber,
            final String type,
            final LocalDate releaseDate
    ) {
        this.platNumber = platNumber;
        this.type = type;
        this.releaseDate = releaseDate;
    }

    public static Either<Collection<? extends CarError>, Car> of(
            final String platNumber,
            final String type,
            final LocalDate releaseDate
    ) {
        final var checkResult =
                checkInput(
                        platNumber,
                        type,
                        releaseDate
                );
        return
                checkResult.isEmpty()
                        ?
                        Either.right(
                                new Car(
                                        platNumber,
                                        type,
                                        releaseDate
                                )
                        )
                        :
                        Either.left(checkResult);
    }

    private static Collection<? extends CarError> checkInput(
            final String platNumber,
            final String type,
            final LocalDate releaseDate
    ) {
        return
                Stream
                        .of(
                                checkPlateNumber(platNumber),
                                checkType(type),
                                checkReleaseDate(releaseDate)
                        )
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toUnmodifiableSet());
    }

    private static Optional<? extends CarError> checkReleaseDate(final LocalDate releaseDate) {
        return
                checkDateOrError(
                        releaseDate,
                        CarError.ReleaseDateError::new
                );
    }

    private static Optional<? extends CarError> checkDateOrError(
            final LocalDate localDate,
            final Supplier<? extends CarError> errorSupplier) {
        return
                checkDate(localDate)
                        ? Optional.empty()
                        : Optional.of(errorSupplier.get());
    }

    private static boolean checkDate(final LocalDate localDate) {
        return
                Objects.nonNull(localDate) &&
                        checkDateAfterNow(localDate);
    }

    private static boolean checkDateAfterNow(LocalDate localDate) {
        return
                !localDate.isAfter(LocalDate.now());
    }

    private static Optional<? extends CarError> checkType(final String type) {
        return
                checkStringOrError(
                        type,
                        CarError.TypeError::new
                );
    }

    private static Optional<? extends CarError> checkPlateNumber(final String platNumber) {
        return
                checkStringOrError(
                        platNumber,
                        CarError.PlateNumberError::new
                );
    }

    private static Optional<? extends CarError> checkStringOrError(
            final String string,
            final Supplier<? extends CarError> errorSupplier
    ) {
        return
                checkString(string)
                        ? Optional.empty()
                        : Optional.of(errorSupplier.get());
    }

    private static boolean checkString(String string) {
        return
                Objects.nonNull(string) &&
                        checkStringNotEmpty(string);
    }

    private static boolean checkStringNotEmpty(String string) {
        return !string.isEmpty();
    }

    public sealed interface CarError {

        String message();

        record PlateNumberError(String message) implements CarError {
            public PlateNumberError() {
                this("");
            }
        }

        record TypeError(String message) implements CarError {
            public TypeError() {
                this("");
            }
        }

        record ReleaseDateError(String message) implements CarError {
            public ReleaseDateError() {
                this("");
            }
        }
    }
}
