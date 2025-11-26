package com.nuclearvet.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NuclearVetException extends RuntimeException {
    private final HttpStatus httpStatus;
    
    public NuclearVetException(String mensaje, HttpStatus httpStatus) {
        super(mensaje);
        this.httpStatus = httpStatus;
    }
    
    public NuclearVetException(String mensaje, Throwable causa, HttpStatus httpStatus) {
        super(mensaje, causa);
        this.httpStatus = httpStatus;
    }
}
