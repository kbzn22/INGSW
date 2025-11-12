package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.Ingreso;
import java.util.*;

public interface IIngresoRepository {

    void save(Ingreso ingreso);
    boolean existsById(String id);
    void clear();
    List<Ingreso> findByEstadoPendiente();
}

