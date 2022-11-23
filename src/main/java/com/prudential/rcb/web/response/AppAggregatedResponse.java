package com.prudential.rcb.web.response;

import com.prudential.rcb.data.exception.ErrorCode;

import lombok.Data;

@Data
public class AppAggregatedResponse<T> {

    private boolean isSuccess;
    private ErrorCode errorCode;
    private String errorMsg;
    private T data;


    public AppAggregatedResponse(T data) {
        this.isSuccess = true;
        this.data = data;
    }

    public AppAggregatedResponse(ErrorCode errorCode) {
        this.isSuccess = false;
        this.errorCode = errorCode;
    }

    public AppAggregatedResponse(String errorMsg) {
        this.isSuccess = false;
        this.errorMsg = errorMsg;
    }
}
