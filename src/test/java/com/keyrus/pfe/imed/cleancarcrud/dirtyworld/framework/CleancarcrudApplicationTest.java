package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.framework;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ContextConfiguration(initializers = {Initializer.class})
public class CleancarcrudApplicationTest {

    @Test
    void contextLoad() {

    }
}