package com.nuclearvet.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción para errores de validación de negocio.
 */
public class ValidacionException extends NuclearVetException {
    
    public ValidacionException(String mensaje) {
        super(mensaje, HttpStatus.BAD_REQUEST);
    }
}
