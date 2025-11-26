package com.nuclearvet.common.exception;

import org.springframework.http.HttpStatus;

public class RecursoNoEncontradoException extends NuclearVetException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje, HttpStatus.NOT_FOUND);
    }
    
    public RecursoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa, HttpStatus.NOT_FOUND);
    }
}
