package com.grupo1.ingsw_app.persistance;

import com.grupo1.ingsw_app.domain.Doctor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DoctorRepository {
    private final Map<String, Doctor> byUsername = new ConcurrentHashMap<>();
    public void save(Doctor d) {
        byUsername.put(d.getUsuario().getUsuario(), d);
    }
    public Optional<Doctor> findByUsername(String username) {
        return Optional.ofNullable(byUsername.get(username));
    }


}
