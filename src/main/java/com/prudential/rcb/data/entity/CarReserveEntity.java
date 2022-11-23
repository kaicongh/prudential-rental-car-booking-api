package com.prudential.rcb.data.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class CarReserveEntity {

    private final UUID reserveId = UUID.randomUUID();
    private UUID carId;
    private LocalDateTime reserveTime;
    private int reserveDays;

}
