package com.prudential.rcb.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class ApplicationProps {

    private List<RentalCarProps> rentalCars;

    @Data
    public static class RentalCarProps {

        private String uuid;
        private String model;
        private int stock;
    }

}
