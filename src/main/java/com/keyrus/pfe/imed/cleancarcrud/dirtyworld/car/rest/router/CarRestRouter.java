package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.rest.router;

import com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.rest.handler.CarRestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CarRestRouter {

    private final CarRestHandler carRestHandler;

    public CarRestRouter(@Autowired final CarRestHandler carRestHandler) {
        this.carRestHandler = carRestHandler;
    }

    @Bean
    public RouterFunction<ServerResponse> carRoute() {
        return RouterFunctions
                .route()
                .GET("/", serverRequest -> carRestHandler.getAllCars())
                .GET("/plateNumber/{plateNumber}", carRestHandler::getCarByPlatNumber)
                .GET("/type/{type}", carRestHandler::getAllCarsByType)
                .GET("/releaseYear/{releaseYear}", carRestHandler::getAllCarsByReleaseYear)
                .POST("/save", carRestHandler::saveCar)
                .PUT("/update", carRestHandler::updateCar)
                .POST("/delete", carRestHandler::deleteCar)
                .DELETE("/deleteAll", serverRequest -> carRestHandler.deleteAllCars())
                .DELETE("/delete/{plateNumber}", carRestHandler::deleteCarByPlatNumber)
                .build();
    }
}
