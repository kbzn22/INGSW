package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.domain.Persona;
import com.grupo1.ingsw_app.security.Sesion;

import java.util.Optional;

public interface ISesionRepository {
    public void save(Sesion s);
    public Optional<Sesion> find(String id);
   ;

    void delete(String id);
    void deleteByPersona(String cuilPersona);
}
