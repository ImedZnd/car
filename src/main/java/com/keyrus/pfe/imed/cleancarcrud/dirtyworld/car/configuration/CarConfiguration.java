package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.configuration;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.repository.CarRepository;
import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.service.CarService;
import com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.repository.InMemoryCarRepository;
import com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.rest.handler.CarRestHandler;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

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

@AllArgsConstructor
@Service
class QueueInitializer {

    @Autowired
    private final RabbitAdmin rabbitAdmin;
    private final CarEventSettings carEventSettings;

    @EventListener(ApplicationReadyEvent.class)
    public void onStart() {
        createCommunicationPipe(
                rabbitAdmin,
                carEventSettings.save().queue(),
                carEventSettings.save().exchange(),
                carEventSettings.save().routingkey()
        );
        createCommunicationPipe(
                rabbitAdmin,
                carEventSettings.update().queue(),
                carEventSettings.update().exchange(),
                carEventSettings.update().routingkey()
        );
        createCommunicationPipe(
                rabbitAdmin,
                carEventSettings.delete().queue(),
                carEventSettings.delete().exchange(),
                carEventSettings.delete().routingkey()
        );
    }

    private void createCommunicationPipe(
            final RabbitAdmin rabbitAdmin,
            final String queueName,
            final String exchangeName,
            final String routingkey
    ) {
        final var queue = new Queue(queueName);
        rabbitAdmin.declareQueue(queue);
        final var exchange = ExchangeBuilder.directExchange(exchangeName).build();
        rabbitAdmin.declareExchange(exchange);
        final var binding = BindingBuilder.bind(queue).to(exchange).with(routingkey).noargs();
        rabbitAdmin.declareBinding(binding);
    }
}