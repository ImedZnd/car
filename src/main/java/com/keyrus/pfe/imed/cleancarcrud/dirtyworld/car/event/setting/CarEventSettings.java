package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.event.setting;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "car.event")
public record CarEventSettings(
        Event save,
        Event update,
        Event delete
) {
    public record Event(String queue,String exchange, String routingkey) {
    }
}
