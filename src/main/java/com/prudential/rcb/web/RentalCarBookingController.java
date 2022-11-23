package com.prudential.rcb.web;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.prudential.rcb.data.dto.CarReserveDTO;
import com.prudential.rcb.data.dto.RentalCarDTO;
import com.prudential.rcb.web.request.ReserveCarRequest;
import com.prudential.rcb.web.response.AppAggregatedResponse;


public interface RentalCarBookingController {

    @GetMapping(value = "/api/cars")
    AppAggregatedResponse<List<RentalCarDTO>> getAllRentalCars();

    @GetMapping(value = "/api/cars/{carId}/reserves/")
    AppAggregatedResponse<List<CarReserveDTO>> getAllCarReserves(@PathVariable("carId") UUID carId);

    @PostMapping(value = "/api/cars/{carId}/reserves/")
    AppAggregatedResponse<CarReserveDTO> reserveCar(@PathVariable("carId") UUID carId, @Valid @RequestBody ReserveCarRequest request);

    @DeleteMapping(value = "/api/cars/{carId}/reserves/{reserveId}")
    AppAggregatedResponse<List<CarReserveDTO>> returnCar(@PathVariable("carId") UUID carId, @PathVariable("reserveId") UUID reserveId);

}
