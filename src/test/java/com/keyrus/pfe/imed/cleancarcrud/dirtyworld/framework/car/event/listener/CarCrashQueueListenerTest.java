package com.keyrus.pfe.imed.cleancarcrud.dirtyworld.framework.car.event.listener;

import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.model.Car;
import com.keyrus.pfe.imed.cleancarcrud.cleanworld.car.service.CarService;
import com.keyrus.pfe.imed.cleancarcrud.dirtyworld.car.event.setting.CarEventSettings;
import com.keyrus.pfe.imed.cleancarcrud.dirtyworld.framework.initilizer.Initializer;
import org.junit.jupiter.api.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;

@SpringBootTest
@ContextConfiguration(initializers = {Initializer.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CarCrashQueueListenerTest {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitAdmin rabbitAdmin;
    private final CarEventSettings carEventSettings;
    private final CarService carService;

    CarCrashQueueListenerTest(
            @Autowired final RabbitTemplate rabbitTemplate,
            @Autowired final RabbitAdmin rabbitAdmin,
            @Autowired final CarEventSettings carEventSettings,
            @Autowired final CarService carService
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdmin = rabbitAdmin;
        this.carEventSettings = carEventSettings;
        this.carService = carService;
    }

    @BeforeAll
    public void beforeAll() {
        rabbitAdmin.purgeQueue(carEventSettings.save().queue());
        rabbitAdmin.purgeQueue(carEventSettings.update().queue());
        rabbitAdmin.purgeQueue(carEventSettings.delete().queue());
        rabbitAdmin.purgeQueue("carcrashedqueue");
        carService.deleteAllCars();
    }

    @BeforeEach
    public void beforeEach() {
        rabbitAdmin.purgeQueue(carEventSettings.save().queue());
        rabbitAdmin.purgeQueue(carEventSettings.update().queue());
        rabbitAdmin.purgeQueue(carEventSettings.delete().queue());
        rabbitAdmin.purgeQueue("carcrashedqueue");
        carService.deleteAllCars();
    }

    @AfterEach
    public void afterEach() {
        rabbitAdmin.purgeQueue(carEventSettings.save().queue());
        rabbitAdmin.purgeQueue(carEventSettings.update().queue());
        rabbitAdmin.purgeQueue(carEventSettings.delete().queue());
        rabbitAdmin.purgeQueue("carcrashedqueue");
        carService.deleteAllCars();
    }

    @AfterAll
    public void afterAll() {
        rabbitAdmin.purgeQueue(carEventSettings.save().queue());
        rabbitAdmin.purgeQueue(carEventSettings.update().queue());
        rabbitAdmin.purgeQueue(carEventSettings.delete().queue());
        rabbitAdmin.purgeQueue("carcrashedqueue");
        carService.deleteAllCars();
    }


    @Test
    @DisplayName("on car exist in repo in car crashed listener")
    public void on_car_exist_in_repo_in_car_crashed_listener() throws Exception {
        final var car =
                Car.of(
                                "111TN1111",
                                "BMW",
                                LocalDate.now().minusYears(1)
                        )
                        .get();

        carService.saveCar(car);
        rabbitTemplate.convertAndSend(
                "carcrashedexchange",
                "carcrashedroutingkey",
                car.getPlatNumber()
        );

        Thread.sleep(5000);

        final var result =
                carService.getAllCars().size();

        Assertions.assertEquals(0, result);
    }
}