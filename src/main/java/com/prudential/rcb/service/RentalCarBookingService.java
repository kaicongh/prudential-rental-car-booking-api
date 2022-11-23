package com.prudential.rcb.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.prudential.rcb.config.ApplicationProps;
import com.prudential.rcb.data.dto.CarReserveDTO;
import com.prudential.rcb.data.dto.RentalCarDTO;
import com.prudential.rcb.data.entity.CarReserveEntity;
import com.prudential.rcb.data.entity.RentalCarEntity;
import com.prudential.rcb.data.exception.BusinessCheckException;
import com.prudential.rcb.data.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class RentalCarBookingService {

    private final Map<UUID, RentalCarEntity> cars = new ConcurrentHashMap<>();
    private final Map<UUID, CarReserveEntity> reserves = new ConcurrentHashMap<>();

    public RentalCarBookingService(ApplicationProps applicationProps) {

        applicationProps.getRentalCars().stream()
            .map(props -> new RentalCarEntity(props.getUuid(), props.getModel(), props.getStock()))
            .forEach(car -> cars.put(car.getCarId(), car));

    }

    public List<RentalCarDTO> getAllRentalCars() {
        return cars.values().stream()
            .map(RentalCarDTO::new)
            .collect(Collectors.toList());
    }

    public List<CarReserveDTO> getAllCarReserves(UUID carId) {
        if (!cars.containsKey(carId)) {
            throw new BusinessCheckException(ErrorCode.INVALID_CAR_ID);
        }
        return reserves.values().stream()
            .filter(reserve -> carId.equals(reserve.getCarId()))
            .map(CarReserveDTO::new)
            .collect(Collectors.toList());
    }

    public CarReserveDTO reserveCar(UUID carId, int reserveDays) {

        RentalCarEntity car = cars.get(carId);
        if (Objects.isNull(car)) {
            throw new BusinessCheckException(ErrorCode.INVALID_CAR_ID);
        }
        AtomicInteger available = car.getAvailable();
        if (available.get() < 1) {
            throw new BusinessCheckException(ErrorCode.NO_AVAILABLE_CAR);
        }

        synchronized (car) {
            if (available.get() < 1) {
                throw new BusinessCheckException(ErrorCode.NO_AVAILABLE_CAR);
            }
            CarReserveEntity reserve = new CarReserveEntity();
            UUID reserveId = reserve.getReserveId();
            reserve.setCarId(carId);
            reserve.setReserveTime(LocalDateTime.now());
            reserve.setReserveDays(reserveDays);
            reserves.put(reserveId, reserve);
            available.decrementAndGet();
            return new CarReserveDTO(reserve);
        }
    }

    public void returnCar(UUID carId, UUID reserveId) {

        RentalCarEntity car = cars.get(carId);
        if (Objects.isNull(car)) {
            throw new BusinessCheckException(ErrorCode.INVALID_CAR_ID);
        }
        if (!reserves.containsKey(reserveId)) {
            throw new BusinessCheckException(ErrorCode.INVALID_RESERVE_ID);
        }
        synchronized (car) {
            reserves.remove(reserveId);
            car.getAvailable().incrementAndGet();
        }
    }

}
