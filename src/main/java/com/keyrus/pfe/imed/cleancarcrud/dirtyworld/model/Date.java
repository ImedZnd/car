package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.model;

import io.vavr.control.Either;
import lombok.Data;

import java.time.DateTimeException;
import java.time.LocalDate;

@Data
public final class Date {

    private final int day;
    private final int month;
    private final int year;


    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

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

    public int day() {
        return day;
    }

    public int month() {
        return month;
    }

    public int year() {
        return year;
    }

}