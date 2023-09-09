package com.sonthai.schedulermanagement.exception;

import com.sonthai.schedulermanagement.model.GeneralResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
@Slf4j
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(DomainException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public GeneralResponse<?> handleDomainException(DomainException e, Locale locale) {
        log.error("method: handleDomainException, ex: {}", e.getMessage());

        return new GeneralResponse<>()
                .setStatus(
                        new com.sonthai.schedulermanagement.model.ResponseStatus(
                                e.getDomainCode().getCode(),
                                e.getMessage()));
    }
}
