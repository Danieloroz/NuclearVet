package com.nuclearvet.modulos.administrativo.mapper;

import com.nuclearvet.modulos.administrativo.dto.FacturaDTO;
import com.nuclearvet.modulos.administrativo.dto.CrearFacturaDTO;
import com.nuclearvet.modulos.administrativo.entity.Factura;
import org.mapstruct.*;

/**
 * Mapper para conversión entre Factura y DTOs.
 * RF6.1 - Gestión de facturación
 */
@Mapper(componentModel = "spring", uses = {ItemFacturaMapper.class, PagoMapper.class})
public interface FacturaMapper {

    @Mapping(target = "pacienteId", ignore = true)
    @Mapping(target = "pacienteNombre", ignore = true)
    @Mapping(target = "propietarioId", ignore = true)
    @Mapping(target = "propietarioNombre", ignore = true)
    @Mapping(target = "consultaId", ignore = true)
    @Mapping(target = "emitidaPorId", ignore = true)
    @Mapping(target = "emitidaPorNombre", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    FacturaDTO toDTO(Factura factura);

    @Mapping(target = "numeroFactura", ignore = true)
    @Mapping(target = "paciente", ignore = true)
    @Mapping(target = "propietario", ignore = true)
    @Mapping(target = "consulta", ignore = true)
    @Mapping(target = "emitidaPor", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "pagos", ignore = true)
    @Mapping(target = "estado", constant = "PENDIENTE")
    @Mapping(target = "subtotal", constant = "0")
    @Mapping(target = "valorImpuesto", constant = "0")
    @Mapping(target = "total", constant = "0")
    @Mapping(target = "totalPagado", constant = "0")
    @Mapping(target = "saldoPendiente", constant = "0")
    Factura toEntity(CrearFacturaDTO dto);
}
