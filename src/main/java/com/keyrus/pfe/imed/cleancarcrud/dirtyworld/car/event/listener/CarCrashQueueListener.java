package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.event.listener;

import com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.event.handler.CarCrashQueueHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
public class CarCrashQueueListener {

    @Bean
    public Consumer<Message<String>> carCrashedListener(final CarCrashQueueHandler carCrashQueueHandler) {
        return message -> carCrashQueueHandler.carCrashedHandler(message.getPayload());
    }
}
