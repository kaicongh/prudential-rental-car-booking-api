package com.prudential.rcb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

import com.prudential.rcb.config.ApplicationProps;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProps.class})
public class RentalCarBookingDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentalCarBookingDemoApplication.class, args);
    }

}
