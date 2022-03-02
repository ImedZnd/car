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
    public CarRepository InMemoryCarRepository(){
        return InMemoryCarRepository.getInstance();
    }

    @Bean
    public CarService CarService(final CarRepository carRepository){ return CarService.getInstance(carRepository);}

    @Bean
    public CarRestHandler CarRestHandler(final CarService carService){ return CarRestHandler.instance(carService);}
}
