package com.grupo1.ingsw_app.persistance;

import com.grupo1.ingsw_app.security.SesionActual;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SesionRepository {
    private final Map<String, SesionActual> sessions = new ConcurrentHashMap<>();

    public void save(SesionActual s){ sessions.put(s.getId(), s); }
    public Optional<SesionActual> find(String id){
        SesionActual s = sessions.get(id);
        if (s == null || s.isExpired()) return Optional.empty();
        return Optional.of(s);
    }
    public void delete(String id){ sessions.remove(id); }
}
