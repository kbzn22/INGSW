package com.grupo1.ingsw_app.persistance;

import com.grupo1.ingsw_app.domain.Ingreso;
import java.util.*;

public interface IIngresoRepository {
    void save(Ingreso ingreso);
    Optional<Ingreso> findById(UUID id);
    List<Ingreso> findAll();
    boolean existsById(UUID id);
    boolean estaEnCola(Ingreso ingreso);
    void clear();
}

