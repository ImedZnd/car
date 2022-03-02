package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.dto;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.model.Car;
import com.keyrus.pfe.imed.cleancarcrud.dirtyworld.model.Date;
import io.vavr.control.Either;
import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
public class CarDTO {

    public CarDTO(String platNumber, String type, Date releaseDate) {
        this.platNumber = platNumber;
        this.type = type;
        this.releaseDate = releaseDate;
    }

    private final String platNumber;
    private final String type;
    private final Date releaseDate;

    public CarDTO(final Car car) {
        this.platNumber = car.getPlatNumber();
        this.type = car.getType();
        this.releaseDate = new Date(car.getReleaseDate());
    }

    public Either<Collection<? extends Car.CarError>, Car> toCar() {
        return
                releaseDate
                        .toLocalDate()
                        .fold(
                                it -> Either.left(List.of(new Car.CarError.ReleaseDateError())),
                                it ->
                                        Car.of(
                                                platNumber,
                                                type,
                                                it
                                        )
                        );
    }

}
