package com.grupo1.ingsw_app.persistance;

import com.grupo1.ingsw_app.domain.EstadoIngreso;
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
    public boolean existsById(String id) {
        return data.containsKey(id);
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public List<Ingreso> findByEstadoPendiente() {
        List<Ingreso> out = new ArrayList<>();
        for (Ingreso i : data.values()) {
            if (i.getEstadoIngreso() == EstadoIngreso.PENDIENTE) out.add(i);
        }
        return out;
    }


}
