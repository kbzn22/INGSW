package com.grupo1.ingsw_app.persistance;

import com.grupo1.ingsw_app.domain.Ingreso;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class IngresoRepository implements IIngresoRepository {
    private final Map<UUID, Ingreso> data = new LinkedHashMap<>();

    @Override
    public void save(Ingreso ingreso) {
        if (ingreso.getId() == null) ingreso.setId(UUID.randomUUID());
        data.put(ingreso.getId(), ingreso);
    }

    @Override
    public Optional<Ingreso> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Ingreso> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Ingreso> findByEstado(String estado) {
        return data.values().stream()
                .filter(i -> i.getEstadoIngreso().name().equalsIgnoreCase(estado))
                .toList();
    }

    @Override public boolean existsById(UUID id) {
        return data.containsKey(id);
    }
}
