package com.prudential.rcb.data.entity;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RentalCarEntity {

    public RentalCarEntity(String uuid, String model, int stock) {
        this.carId = UUID.fromString(uuid);
        this.model = model;
        this.stock = stock;
        this.available = new AtomicInteger(stock);
    }

    private final UUID carId;
    private final String model;
    private final int stock;
    private final AtomicInteger available;
}
