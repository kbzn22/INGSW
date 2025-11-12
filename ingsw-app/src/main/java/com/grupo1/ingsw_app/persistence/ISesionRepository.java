package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.security.Sesion;

import java.util.Optional;

public interface ISesionRepository {
    public void save(Sesion s);
    public Optional<Sesion> find(String id);
    public void delete(String id);
}
