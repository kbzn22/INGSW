package com.grupo1.ingsw_app.persistance;

import com.grupo1.ingsw_app.domain.Enfermera;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class EnfermeraRepository {
    private final Map<String, Enfermera> byUsername = new ConcurrentHashMap<>();
    public void save(Enfermera e) {
        byUsername.put(e.getUsuario().getUsuario(), e);
    }
    public Optional<Enfermera> findByUsername(String username) {
        return Optional.ofNullable(byUsername.get(username));
    }
}
