
package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.Atencion;

import java.util.Optional;
import java.util.UUID;

public interface IAtencionRepository {

    void save(Atencion atencion);

    Optional<Atencion> findByIngresoId(UUID ingresoId);
}
