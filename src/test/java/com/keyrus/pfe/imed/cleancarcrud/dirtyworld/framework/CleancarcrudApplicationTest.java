package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.framework;

import com.keyrus.pfe.imed.cleancarcrud.dirtyworld.framework.initilizer.Initializer;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(initializers = {Initializer.class})
public class CleancarcrudApplicationTest {

    @Test
    void contextLoad() {

    }
}