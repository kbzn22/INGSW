package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.EstadoIngreso;
import com.grupo1.ingsw_app.domain.Ingreso;

import java.time.LocalDateTime;
import java.util.*;

public interface IIngresoRepository {

    List<Ingreso> findForLog(LocalDateTime desde,
                             LocalDateTime hasta,
                             String cuilPaciente,
                             String cuilEnfermera);

    void save(Ingreso ingreso);
    boolean existsById(String id);
    void clear();
    Optional<Ingreso> findById(UUID id);
    List<Ingreso> findByEstado(EstadoIngreso estado);
    Optional<Ingreso> findFirstEnProceso();
    Optional<Ingreso> findEnAtencionActual();
    int countByEstado(EstadoIngreso estado);
    List<Ingreso> findDetallesParaExport(
            LocalDateTime desde,
            LocalDateTime hasta,
            String cuilPaciente,
            String cuilEnfermera
    );
}