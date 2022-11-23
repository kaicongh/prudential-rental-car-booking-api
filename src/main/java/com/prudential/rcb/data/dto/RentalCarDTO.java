package com.prudential.rcb.data.dto;

import java.util.UUID;

import com.prudential.rcb.data.entity.RentalCarEntity;

import lombok.Data;

@Data
public class RentalCarDTO {

    private UUID carId;
    private String model;
    private int stock;
    private int available;

    public RentalCarDTO() {
    }

    public RentalCarDTO(RentalCarEntity entity) {

        this.carId = entity.getCarId();
        this.model = entity.getModel();
        this.stock = entity.getStock();
        this.available = entity.getAvailable().get();
    }
}
