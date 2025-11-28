package com.nuclearvet.modulos.notificaciones.entity;

/**
 * Tipos de notificaciones soportadas por el sistema.
 */
public enum TipoNotificacion {
    /**
     * Recordatorio de cita programada
     */
    RECORDATORIO_CITA,
    
    /**
     * Confirmación de cita agendada
     */
    CONFIRMACION_CITA,
    
    /**
     * Cancelación de cita
     */
    CANCELACION_CITA,
    
    /**
     * Recordatorio de vacunación
     */
    RECORDATORIO_VACUNA,
    
    /**
     * Alerta de medicamento por vencer
     */
    ALERTA_MEDICAMENTO,
    
    /**
     * Alerta de stock bajo
     */
    ALERTA_STOCK,
    
    /**
     * Resultado de exámenes disponible
     */
    RESULTADO_EXAMENES,
    
    /**
     * Notificación general del sistema
     */
    NOTIFICACION_GENERAL
}
