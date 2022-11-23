package com.prudential.rcb.service;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.prudential.rcb.config.ApplicationProps;
import com.prudential.rcb.data.dto.CarReserveDTO;
import com.prudential.rcb.data.dto.RentalCarDTO;
import com.prudential.rcb.data.entity.CarReserveEntity;
import com.prudential.rcb.data.entity.RentalCarEntity;
import com.prudential.rcb.data.exception.BusinessCheckException;
import com.prudential.rcb.data.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
public class RentalCarBookingServiceTest {

    private RentalCarBookingService carBookingService;

    @Mock
    ApplicationProps applicationProps;

    @BeforeEach
    public void setup() {

        List<ApplicationProps.RentalCarProps> carPropsList = new ArrayList<>();

        ApplicationProps.RentalCarProps props1 = new ApplicationProps.RentalCarProps();
        props1.setUuid("55bb9ebd-3929-4acc-bce0-8d7433abb565");
        props1.setModel("Model A");
        props1.setStock(2);
        carPropsList.add(props1);

        ApplicationProps.RentalCarProps props2 = new ApplicationProps.RentalCarProps();
        props2.setUuid("aac5c2df-3bcd-4341-9fe4-4936c75efb49");
        props2.setModel("Model B");
        props2.setStock(3);
        carPropsList.add(props2);

        Mockito.when(applicationProps.getRentalCars()).thenReturn(carPropsList);
        this.carBookingService = new RentalCarBookingService(applicationProps);
    }

    @Test
    public void testGetAllRentalCars() {

        List<RentalCarDTO> cars = carBookingService.getAllRentalCars();
        Assertions.assertEquals(2, cars.size());
        Assertions.assertTrue(
            cars.stream().anyMatch(
                c -> "55bb9ebd-3929-4acc-bce0-8d7433abb565".equals(c.getCarId().toString())
                    && "Model A".equals(c.getModel())
                    && 2 == c.getStock()
                    && 2 == c.getAvailable()));
        Assertions.assertTrue(
            cars.stream().anyMatch(
                c -> "aac5c2df-3bcd-4341-9fe4-4936c75efb49".equals(c.getCarId().toString())
                    && "Model B".equals(c.getModel())
                    && 3 == c.getStock()
                    && 3 == c.getAvailable()));
    }

    @Test
    public void testReserveCarSuccess() throws NoSuchFieldException, IllegalAccessException {

        CarReserveDTO reserve = carBookingService.reserveCar(UUID.fromString("55bb9ebd-3929-4acc-bce0-8d7433abb565"), 3);

        Field carsField = RentalCarBookingService.class.getDeclaredField("cars");
        carsField.setAccessible(true);
        Map<UUID, RentalCarEntity> cars = (Map<UUID, RentalCarEntity>) carsField.get(carBookingService);
        RentalCarEntity reservedCar = cars.get(UUID.fromString("55bb9ebd-3929-4acc-bce0-8d7433abb565"));
        Assertions.assertEquals(1, reservedCar.getAvailable().get());

        Field reservesField = RentalCarBookingService.class.getDeclaredField("reserves");
        reservesField.setAccessible(true);
        Map<UUID, CarReserveEntity> reserves = (Map<UUID, CarReserveEntity>) reservesField.get(carBookingService);
        Assertions.assertEquals(1, reserves.size());
        Assertions.assertTrue(reserves.values().stream()
            .anyMatch(r -> "55bb9ebd-3929-4acc-bce0-8d7433abb565".equals(r.getCarId().toString())
                && 3 == r.getReserveDays()));
        Assertions.assertEquals("55bb9ebd-3929-4acc-bce0-8d7433abb565", reserve.getCarId().toString());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime reserveTime = LocalDateTime.parse(reserve.getReserveTime(), formatter);
        LocalDateTime expectedReturnTime = LocalDateTime.parse(reserve.getExpectedReturnTime(), formatter);

        Assertions.assertEquals(3, reserveTime.until(expectedReturnTime, ChronoUnit.DAYS));


    }

    @Test
    public void testReserveCarInvalidCarId() {

        BusinessCheckException ex = Assertions.assertThrows(BusinessCheckException.class,
            () -> carBookingService.reserveCar(UUID.fromString("84de621b-c1ec-4d51-b746-e2906c8a7e9b"), 1));
        Assertions.assertEquals(ErrorCode.INVALID_CAR_ID, ex.getErrorCode());
    }

    @Test
    public void testReserveCarNoAvailableCar() {

        carBookingService.reserveCar(UUID.fromString("55bb9ebd-3929-4acc-bce0-8d7433abb565"), 1);
        carBookingService.reserveCar(UUID.fromString("55bb9ebd-3929-4acc-bce0-8d7433abb565"), 1);

        BusinessCheckException ex = Assertions.assertThrows(BusinessCheckException.class,
            () -> carBookingService.reserveCar(UUID.fromString("55bb9ebd-3929-4acc-bce0-8d7433abb565"), 1));
        Assertions.assertEquals(ErrorCode.NO_AVAILABLE_CAR, ex.getErrorCode());
    }

    @Test
    public void testReturnCarSuccess() throws NoSuchFieldException, IllegalAccessException {

        CarReserveDTO reserve = carBookingService.reserveCar(UUID.fromString("55bb9ebd-3929-4acc-bce0-8d7433abb565"), 1);
        UUID reserveId = reserve.getReserveId();

        carBookingService.returnCar(UUID.fromString("55bb9ebd-3929-4acc-bce0-8d7433abb565"), reserveId);
        Field carsField = RentalCarBookingService.class.getDeclaredField("cars");
        carsField.setAccessible(true);
        Map<UUID, RentalCarEntity> cars = (Map<UUID, RentalCarEntity>) carsField.get(carBookingService);
        RentalCarEntity reservedCar = cars.get(UUID.fromString("55bb9ebd-3929-4acc-bce0-8d7433abb565"));
        Assertions.assertEquals(2, reservedCar.getAvailable().get());

        Field reservesField = RentalCarBookingService.class.getDeclaredField("reserves");
        reservesField.setAccessible(true);
        Map<UUID, CarReserveEntity> reserves = (Map<UUID, CarReserveEntity>) reservesField.get(carBookingService);
        Assertions.assertEquals(0, reserves.size());
    }

    @Test
    public void testReturnCarInvalidCarId() {

        BusinessCheckException ex = Assertions.assertThrows(BusinessCheckException.class,
            () -> carBookingService.returnCar(UUID.fromString("84de621b-c1ec-4d51-b746-e2906c8a7e9b"), UUID.randomUUID()));
        Assertions.assertEquals(ErrorCode.INVALID_CAR_ID, ex.getErrorCode());
    }

    @Test
    public void testReturnCarInvalidReserveId() {

        BusinessCheckException ex = Assertions.assertThrows(BusinessCheckException.class,
            () -> carBookingService.returnCar(UUID.fromString("55bb9ebd-3929-4acc-bce0-8d7433abb565"), UUID.randomUUID()));
        Assertions.assertEquals(ErrorCode.INVALID_RESERVE_ID, ex.getErrorCode());
    }

    @Test
    public void testGetAllCarReservesSuccess() {
        carBookingService.reserveCar(UUID.fromString("55bb9ebd-3929-4acc-bce0-8d7433abb565"), 2);
        List<CarReserveDTO> reserves = carBookingService.getAllCarReserves(UUID.fromString("55bb9ebd-3929-4acc-bce0-8d7433abb565"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Assertions.assertTrue(reserves.stream().anyMatch(r ->
            "55bb9ebd-3929-4acc-bce0-8d7433abb565".equals(r.getCarId().toString())
                && 2 == LocalDateTime.parse(r.getReserveTime(), formatter)
                .until(LocalDateTime.parse(r.getExpectedReturnTime(), formatter), ChronoUnit.DAYS))
        );
    }

    @Test
    public void testGetAllCarReservesInvalidCarId() {

        carBookingService.reserveCar(UUID.fromString("55bb9ebd-3929-4acc-bce0-8d7433abb565"), 2);
        BusinessCheckException ex = Assertions.assertThrows(BusinessCheckException.class,
            () -> carBookingService.getAllCarReserves(UUID.fromString("84de621b-c1ec-4d51-b746-e2906c8a7e9b")));
        Assertions.assertEquals(ErrorCode.INVALID_CAR_ID, ex.getErrorCode());
    }
}
