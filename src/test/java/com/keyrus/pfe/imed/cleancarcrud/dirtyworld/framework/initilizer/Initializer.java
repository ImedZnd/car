package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.framework.initilizer;

import lombok.SneakyThrows;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.testcontainers.containers.GenericContainer;

@Configuration
public class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @SneakyThrows
    @Override
    public void initialize(final ConfigurableApplicationContext applicationContext) {

        final GenericContainer rabbitMqContainer =
                new GenericContainer<>("rabbitmq:management")
                        .withExposedPorts(5672, 15672);
        System.out.println("starting rabbitmq container = " + rabbitMqContainer.getHost());
        rabbitMqContainer.start();
        while (!rabbitMqContainer.isRunning()) {
            System.out.println("starting  ...");
            Thread.sleep(3000);
        }
        if(rabbitMqContainer.isRunning())
            System.out.println("rabbit is running");
            TestPropertyValues
                .of(
                        "spring.rabbitmq.host=" + rabbitMqContainer.getHost(),
                        "spring.rabbitmq.port=" + rabbitMqContainer.getMappedPort(5672)
                )
                .applyTo(applicationContext.getEnvironment());

        final var applicationContextCloseListener = new ApplicationListener<ContextClosedEvent>() {

            @SneakyThrows
            @Override
            public void onApplicationEvent(ContextClosedEvent event) {
                rabbitMqContainer.stop();
                while (rabbitMqContainer.isRunning())
                    Thread.sleep(3000);
            }
        };

        applicationContext
                .addApplicationListener(applicationContextCloseListener);

    }
}
