package com.nuclearvet.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaExitosa<T> {
    private boolean exitoso;
    private String mensaje;
    private T data;
    
    public static <T> RespuestaExitosa<T> crear(T data) {
        return RespuestaExitosa.<T>builder()
                .exitoso(true)
                .mensaje("Operación exitosa")
                .data(data)
                .build();
    }
    
    public static <T> RespuestaExitosa<T> crear(T data, String mensaje) {
        return RespuestaExitosa.<T>builder()
                .exitoso(true)
                .mensaje(mensaje)
                .data(data)
                .build();
    }
}
