package com.nlw.planner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDateSelectedException extends RuntimeException {
    public InvalidDateSelectedException(String message) {
        super(message);
    }
}
