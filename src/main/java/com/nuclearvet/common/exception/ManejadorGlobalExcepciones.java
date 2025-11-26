package com.nuclearvet.common.exception;

import com.nuclearvet.common.dto.RespuestaError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones.
 * Centraliza el manejo de errores y retorna respuestas HTTP apropiadas.
 * 
 * Patrón: Exception Handler Pattern
 */
@Slf4j
@RestControllerAdvice
public class ManejadorGlobalExcepciones {
    
    @ExceptionHandler(NuclearVetException.class)
    public ResponseEntity<RespuestaError> manejarNuclearVetException(
            NuclearVetException ex, 
            WebRequest request) {
        
        log.error("Error de NuclearVet: {}", ex.getMessage(), ex);
        
        RespuestaError respuesta = RespuestaError.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getHttpStatus().value())
                .error(ex.getHttpStatus().getReasonPhrase())
                .mensaje(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(respuesta, ex.getHttpStatus());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> manejarValidacionArgumentos(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String nombreCampo = ((FieldError) error).getField();
            String mensajeError = error.getDefaultMessage();
            errores.put(nombreCampo, mensajeError);
        });
        
        String mensaje = errores.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
        
        log.warn("Error de validación: {}", mensaje);
        
        RespuestaError respuesta = RespuestaError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Error de Validación")
                .mensaje("Uy parce, hay errores en los datos que enviaste: " + mensaje)
                .path(request.getDescription(false).replace("uri=", ""))
                .detalles(errores)
                .build();
        
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RespuestaError> manejarViolacionRestricciones(
            ConstraintViolationException ex,
            WebRequest request) {
        
        Map<String, String> errores = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));
        
        log.warn("Violación de restricciones: {}", errores);
        
        RespuestaError respuesta = RespuestaError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Violación de Restricciones")
                .mensaje("Los datos no cumplen las restricciones, mijo")
                .path(request.getDescription(false).replace("uri=", ""))
                .detalles(errores)
                .build();
        
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<RespuestaError> manejarCredencialesIncorrectas(
            BadCredentialsException ex,
            WebRequest request) {
        
        log.warn("Intento de acceso con credenciales incorrectas");
        
        RespuestaError respuesta = RespuestaError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("No Autorizado")
                .mensaje("Las credenciales son incorrectas, llave. Revisa bien el usuario y la contraseña")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(respuesta, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RespuestaError> manejarAccesoDenegado(
            AccessDeniedException ex,
            WebRequest request) {
        
        log.warn("Intento de acceso denegado: {}", ex.getMessage());
        
        RespuestaError respuesta = RespuestaError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error("Prohibido")
                .mensaje("No tenés permisos para hacer eso, parcero")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(respuesta, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> manejarExcepcionGeneral(
            Exception ex,
            WebRequest request) {
        
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        
        RespuestaError respuesta = RespuestaError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Error Interno del Servidor")
                .mensaje("Uy parce, algo salió mal. Ya estamos revisando qué pasó")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        
        return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
