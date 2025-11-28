package com.nuclearvet.modulos.notificaciones.entity;

/**
 * Estados posibles de una notificación.
 */
public enum EstadoNotificacion {
    /**
     * Notificación pendiente de envío
     */
    PENDIENTE,
    
    /**
     * Notificación enviada exitosamente
     */
    ENVIADA,
    
    /**
     * Notificación leída por el destinatario
     */
    LEIDA,
    
    /**
     * Error al enviar la notificación
     */
    ERROR,
    
    /**
     * Notificación cancelada
     */
    CANCELADA
}
