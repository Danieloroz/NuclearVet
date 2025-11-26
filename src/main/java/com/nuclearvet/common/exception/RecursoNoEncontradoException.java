package com.nuclearvet.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción lanzada cuando no se encuentra un recurso en la base de datos.
 */
public class RecursoNoEncontradoException extends NuclearVetException {
    
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje, HttpStatus.NOT_FOUND);
    }
    
    public RecursoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa, HttpStatus.NOT_FOUND);
    }
    
    public RecursoNoEncontradoException(String recurso, String campo, Object valor) {
        super(String.format("Uy parce, no se encontró %s con %s: %s", recurso, campo, valor), 
              HttpStatus.NOT_FOUND);
    }
}
