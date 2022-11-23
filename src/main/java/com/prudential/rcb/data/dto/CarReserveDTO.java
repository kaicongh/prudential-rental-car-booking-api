package com.prudential.rcb.data.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

import com.prudential.rcb.data.entity.CarReserveEntity;

import lombok.Data;

@Data
public class CarReserveDTO {

    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private UUID reserveId;
    private UUID carId;
    private String reserveTime;
    private String expectedReturnTime;

    public CarReserveDTO() {
    }

    public CarReserveDTO(CarReserveEntity entity) {

        this.reserveId = entity.getReserveId();
        this.carId = entity.getCarId();
        LocalDateTime reserveTime = entity.getReserveTime();
        if (Objects.nonNull(reserveTime)) {
            this.reserveTime = reserveTime.format(FORMATTER);
            this.expectedReturnTime = reserveTime.plusDays(entity.getReserveDays()).format(FORMATTER);
        }
    }
}
