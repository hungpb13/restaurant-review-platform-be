package com.dev.restaurant.controllers;

import com.dev.restaurant.domain.dtos.ErrorDto;
import com.dev.restaurant.exceptions.BaseException;
import com.dev.restaurant.exceptions.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
@Slf4j
public class ErrorController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception e) {
        log.error("Caught Unexpected Exception: ", e);

        ErrorDto errorDto = ErrorDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Oops, something went wrong!")
                .build();

        return new ResponseEntity<>(
                errorDto,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorDto> handleBaseException(BaseException e) {
        log.error("Caught BaseException: ", e);

        ErrorDto errorDto = ErrorDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Oops, something went wrong!")
                .build();

        return new ResponseEntity<>(
                errorDto,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorDto> handleStorageException(StorageException e) {
        log.error("Caught StorageException: ", e);

        ErrorDto errorDto = ErrorDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Unable to store or retrieve resources at this time")
                .build();

        return new ResponseEntity<>(
                errorDto,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
