package com.prudential.rcb.web.request;


import javax.validation.constraints.Min;

import lombok.Data;

@Data
public class ReserveCarRequest {

    @Min(value = 1L, message = "cannot reserve less than 1 day")
    private int reserveDays;
}
