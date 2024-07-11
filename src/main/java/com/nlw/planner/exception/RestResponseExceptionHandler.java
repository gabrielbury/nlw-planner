package com.nlw.planner.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.net.URISyntaxException;

@RestControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = InvalidDateSelectedException.class)
    protected ResponseEntity<ProblemDetail> handleInvalidDates(RuntimeException ex, WebRequest request) throws Exception {;
        return buildErrorResponse(ex, request, HttpStatus.BAD_REQUEST);
    }


    private ResponseEntity buildErrorResponse(Exception ex, WebRequest request, HttpStatus status) throws Exception {
        return ResponseEntity.of(buildDetail(ex, request, status)).build();
    }

    private ProblemDetail buildDetail (Exception ex, WebRequest request, HttpStatus status) throws Exception {
        var detail = ProblemDetail.forStatusAndDetail(status, ex.getLocalizedMessage());
        detail.setType(new URI(((ServletWebRequest)request).getRequest().getRequestURI()));
        return detail;
    }
}
