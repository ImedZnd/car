package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.framework;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@EnableRabbit
@SpringBootApplication
@ComponentScan("com.keyrus.pfe.imed.cleancarcrud.**")
@ConfigurationPropertiesScan(basePackages = {"com.keyrus.pfe.imed.cleancarcrud.**"})
public class CleancarcrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(CleancarcrudApplication.class, args);
	}
}
