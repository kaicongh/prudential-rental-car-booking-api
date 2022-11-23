package com.prudential.rcb.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.prudential.rcb.data.exception.BusinessCheckException;
import com.prudential.rcb.web.response.AppAggregatedResponse;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = BusinessCheckException.class)
    public ResponseEntity<AppAggregatedResponse> handleBusinessCheckException(BusinessCheckException ex) {

        return new ResponseEntity<>(new AppAggregatedResponse(ex.getErrorCode()), HttpStatus.OK);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<AppAggregatedResponse> handleBusinessCheckException(Exception ex) {

        return new ResponseEntity<>(new AppAggregatedResponse("Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
