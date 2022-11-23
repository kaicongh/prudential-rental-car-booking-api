package com.prudential.rcb.web;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.RestController;

import com.prudential.rcb.data.dto.CarReserveDTO;
import com.prudential.rcb.data.dto.RentalCarDTO;
import com.prudential.rcb.service.RentalCarBookingService;
import com.prudential.rcb.web.request.ReserveCarRequest;
import com.prudential.rcb.web.response.AppAggregatedResponse;

@RestController
public class RentalCarBookingControllerImpl implements RentalCarBookingController {


    private final RentalCarBookingService carBookingService;

    public RentalCarBookingControllerImpl(RentalCarBookingService carBookingService) {
        this.carBookingService = carBookingService;
    }

    @Override
    public AppAggregatedResponse<List<RentalCarDTO>> getAllRentalCars() {
        List<RentalCarDTO> cars = carBookingService.getAllRentalCars();
        return new AppAggregatedResponse<>(cars);
    }

    @Override
    public AppAggregatedResponse<List<CarReserveDTO>> getAllCarReserves(UUID carId) {
        List<CarReserveDTO> allReserves = carBookingService.getAllCarReserves(carId);
        return new AppAggregatedResponse<>(allReserves);
    }

    @Override
    public AppAggregatedResponse<CarReserveDTO> reserveCar(UUID carId, @Valid ReserveCarRequest request) {
        int reserveDays = request.getReserveDays();
        CarReserveDTO reserve = carBookingService.reserveCar(carId, reserveDays);
        return new AppAggregatedResponse<>(reserve);
    }

    @Override
    public AppAggregatedResponse<List<CarReserveDTO>> returnCar(UUID carId, UUID reserveId) {
        carBookingService.returnCar(carId, reserveId);
        List<CarReserveDTO> allReserves = carBookingService.getAllCarReserves(carId);
        return new AppAggregatedResponse<>(allReserves);
    }
}
