package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.EstadoIngreso;
import com.grupo1.ingsw_app.domain.Ingreso;
import java.util.*;

public interface IIngresoRepository {

    void save(Ingreso ingreso);
    boolean existsById(String id);
    void clear();
    List<Ingreso> findByEstadoPendiente();
    Optional<Ingreso> findById(UUID id);
    List<Ingreso> findByEstado(EstadoIngreso estado);
    Optional<Ingreso> findFirstEnProceso();
    Optional<Ingreso> findEnAtencionActual();
    int countByEstado(EstadoIngreso estado);
}

