package com.prudential.rcb.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.prudential.rcb.data.dto.CarReserveDTO;
import com.prudential.rcb.data.dto.RentalCarDTO;
import com.prudential.rcb.data.exception.BusinessCheckException;
import com.prudential.rcb.data.exception.ErrorCode;
import com.prudential.rcb.service.RentalCarBookingService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = RentalCarBookingController.class)
public class RentalCarBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalCarBookingService mockCarBookingService;

    @Test
    public void testReserveCarSuccess() throws Exception {

        CarReserveDTO reserve = new CarReserveDTO();
        reserve.setReserveId(UUID.fromString("5b1ca051-4f91-4be0-802b-bb5345fe66fc"));
        reserve.setCarId(UUID.fromString("71f7a41a-f47e-4b85-8526-ff376bdb2834"));
        reserve.setReserveTime("2022-11-23 08:57:51");
        reserve.setExpectedReturnTime("2022-11-24 08:57:51");
        Mockito.when(mockCarBookingService.reserveCar(UUID.fromString("71f7a41a-f47e-4b85-8526-ff376bdb2834"), 1))
            .thenReturn(reserve);
        mockMvc.perform(
            post("/api/cars/71f7a41a-f47e-4b85-8526-ff376bdb2834/reserves/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reserveDays\" : 1}"))
            .andExpect(status().isOk())
            .andExpect(content().json(
                "{\"errorCode\":null,\"errorMsg\":null,\"data\":{\"reserveId\":\"5b1ca051-4f91-4be0-802b-bb5345fe66fc\",\"carId\":\"71f7a41a-f47e-4b85-8526-ff376bdb2834\",\"reserveTime\":\"2022-11-23 08:57:51\",\"expectedReturnTime\":\"2022-11-24 08:57:51\"},\"success\":true}"));
    }

    @Test
    public void testReserveCarBadRequest() throws Exception {
        mockMvc.perform(
            post("/api/cars/71f7a41a-f47e-4b85-8526-ff376bdb2834/reserves/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reserveDays\" : 0}"))
            .andExpect(status().isBadRequest());

    }

    @Test
    public void testReserveCarBusinessCheckException() throws Exception {

        Mockito
            .when(mockCarBookingService.reserveCar(UUID.fromString("71f7a41a-f47e-4b85-8526-ff376bdb2834"), 1))
            .thenThrow(new BusinessCheckException(ErrorCode.NO_AVAILABLE_CAR));

        mockMvc.perform(
            post("/api/cars/71f7a41a-f47e-4b85-8526-ff376bdb2834/reserves/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reserveDays\" : 1}"))
            .andExpect(status().isOk())
            .andExpect(content().json("{\"errorCode\":\"NO_AVAILABLE_CAR\",\"errorMsg\":null,\"data\":null,\"success\":false}"));
    }

    @Test
    public void testReserveCarInternalServerError() throws Exception {

        Mockito
            .when(mockCarBookingService.reserveCar(UUID.fromString("71f7a41a-f47e-4b85-8526-ff376bdb2834"), 1))
            .thenThrow(new RuntimeException("test error"));

        mockMvc.perform(
            post("/api/cars/71f7a41a-f47e-4b85-8526-ff376bdb2834/reserves/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"reserveDays\" : 1}"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().json("{\"errorCode\":null,\"errorMsg\":\"Internal Server Error\",\"data\":null,\"success\":false}"));
    }

    @Test
    public void testGetAllRentalCars() throws Exception {

        RentalCarDTO car = new RentalCarDTO();
        car.setCarId(UUID.fromString("71f7a41a-f47e-4b85-8526-ff376bdb2834"));
        car.setModel("test model");
        car.setStock(2);
        car.setAvailable(1);
        Mockito.when(mockCarBookingService.getAllRentalCars())
            .thenReturn(List.of(car));
        mockMvc.perform(get("/api/cars/"))
            .andExpect(status().isOk())
            .andExpect(content().json("{\"errorCode\":null,\"errorMsg\":null,\"data\":[{\"carId\":\"71f7a41a-f47e-4b85-8526-ff376bdb2834\",\"model\":\"test model\",\"stock\":2,\"available\":1}],\"success\":true}"));
    }

    @Test
    public void testGetAllCarReserves() throws Exception {

        CarReserveDTO reserve = new CarReserveDTO();
        reserve.setReserveId(UUID.fromString("5b1ca051-4f91-4be0-802b-bb5345fe66fc"));
        reserve.setCarId(UUID.fromString("71f7a41a-f47e-4b85-8526-ff376bdb2834"));
        reserve.setReserveTime("2022-11-23 08:57:51");
        reserve.setExpectedReturnTime("2022-11-24 08:57:51");
        Mockito.when(mockCarBookingService.getAllCarReserves(UUID.fromString("71f7a41a-f47e-4b85-8526-ff376bdb2834")))
            .thenReturn(List.of(reserve));
        mockMvc.perform(get("/api/cars/71f7a41a-f47e-4b85-8526-ff376bdb2834/reserves/"))
            .andExpect(status().isOk())
            .andExpect(content().json(
                "{\"errorCode\":null,\"errorMsg\":null,\"data\":[{\"reserveId\":\"5b1ca051-4f91-4be0-802b-bb5345fe66fc\",\"carId\":\"71f7a41a-f47e-4b85-8526-ff376bdb2834\",\"reserveTime\":\"2022-11-23 08:57:51\",\"expectedReturnTime\":\"2022-11-24 08:57:51\"}],\"success\":true}"));
    }

    @Test
    public void testReturnCar() throws Exception {

        Mockito.when(mockCarBookingService.getAllCarReserves(UUID.fromString("71f7a41a-f47e-4b85-8526-ff376bdb2834")))
            .thenReturn(List.of());
        mockMvc.perform(delete("/api/cars/71f7a41a-f47e-4b85-8526-ff376bdb2834/reserves/5b1ca051-4f91-4be0-802b-bb5345fe66fc/"))
            .andExpect(status().isOk())
            .andExpect(content().json("{\"errorCode\":null,\"errorMsg\":null,\"data\":[],\"success\":true}"));
    }

}
