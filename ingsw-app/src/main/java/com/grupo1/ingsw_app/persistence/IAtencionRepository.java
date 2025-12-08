
package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.Atencion;
import com.grupo1.ingsw_app.dtos.AtencionLogDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IAtencionRepository {

    void save(Atencion atencion);

    Optional<Atencion> findByIngresoId(UUID ingresoId);

    List<AtencionLogDTO> findLogs(LocalDateTime d, LocalDateTime h, String cuilDoctor, String cuilPaciente);

    List<Atencion> findForLog(LocalDateTime desde,
                              LocalDateTime hasta,
                              String cuilDoctor);

    Optional<Atencion> findById(UUID id);
}
