package com.nuclearvet.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción para conflictos de datos (ej: email duplicado, cita ya agendada).
 */
public class ConflictoException extends NuclearVetException {
    
    public ConflictoException(String mensaje) {
        super(mensaje, HttpStatus.CONFLICT);
    }
}
