package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.model;

import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class Date {

    private int day;
    private int month;
    private int year;

    public Date(final LocalDate date) {
        this.day = date.getDayOfMonth();
        this.month = date.getMonthValue();
        this.year = date.getYear();
    }

    public Either<DateTimeException, LocalDate> toLocalDate() {
        try {
            return
                    Either.right(
                            LocalDate.of(
                                    year,
                                    month,
                                    day
                            )
                    );
        } catch (DateTimeException dateTimeException) {
            return
                    Either.left(dateTimeException);
        }
    }

}