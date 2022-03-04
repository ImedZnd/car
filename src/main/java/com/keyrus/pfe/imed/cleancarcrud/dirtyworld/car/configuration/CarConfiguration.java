package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.configuration;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.repository.CarRepository;
import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.service.CarService;
import com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.repository.InMemoryCarRepository;
import com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.rest.handler.CarRestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CarConfiguration {

    @Bean
    public CarRepository inMemoryCarRepository() {
        return InMemoryCarRepository.getInstance();
    }

    @Bean
    public CarService carService(final CarRepository carRepository) {
        return CarService.getInstance(carRepository);
    }

    @Bean
    public CarRestHandler carRestHandler(final CarService carService) {
        return CarRestHandler.instance(carService);
    }
}
