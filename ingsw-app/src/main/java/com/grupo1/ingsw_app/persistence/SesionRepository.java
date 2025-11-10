package com.grupo1.ingsw_app.persistence;

import com.grupo1.ingsw_app.security.Sesion;


import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SesionRepository {
    private final Map<String, Sesion> sessions = new ConcurrentHashMap<>();

    public void save(Sesion s){ sessions.put(s.getId(), s); }
    public Optional<Sesion> find(String id){
        Sesion s = sessions.get(id);
        if (s == null || s.isExpired()) return Optional.empty();
        return Optional.of(s);
    }
    public void delete(String id){ sessions.remove(id); }
}
