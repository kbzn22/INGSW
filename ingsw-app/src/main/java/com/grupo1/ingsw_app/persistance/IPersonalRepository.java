package com.grupo1.ingsw_app.persistance;

import com.grupo1.ingsw_app.domain.Enfermera;
import com.grupo1.ingsw_app.domain.valueobjects.Cuil;

import java.util.Optional;

public interface IPersonalRepository {
    Optional<Enfermera> findByCuil(String cuil);

    void save(Enfermera enfermeraActual);

    void clear();
}
