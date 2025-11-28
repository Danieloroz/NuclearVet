package com.nuclearvet.modulos.notificaciones.entity;

/**
 * Canales disponibles para envío de notificaciones.
 */
public enum CanalNotificacion {
    /**
     * Notificación por correo electrónico
     */
    EMAIL,
    
    /**
     * Notificación por mensaje de texto SMS
     */
    SMS,
    
    /**
     * Notificación dentro de la aplicación
     */
    IN_APP,
    
    /**
     * Notificación push (móvil)
     */
    PUSH
}
