package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.event.handler;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.service.CarService;

import java.util.Objects;

public final class CarCrashQueueHandler {

    private static CarCrashQueueHandler instance = null;

    public static synchronized CarCrashQueueHandler instance(final CarService carService) {
        if (Objects.isNull(instance))
            instance = new CarCrashQueueHandler(carService);
        return instance;
    }

    private final CarService carService;

    private CarCrashQueueHandler(final CarService carService) {
        this.carService = carService;
    }

    public void carCrashedHandler(final String plateNumber) {
        carService.deleteCar(plateNumber);
    }

}
