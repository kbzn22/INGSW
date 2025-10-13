package com.grupo1.ingsw_app.persistance;

import com.grupo1.ingsw_app.domain.Enfermera;
import com.grupo1.ingsw_app.domain.Paciente;
import com.grupo1.ingsw_app.domain.valueobjects.Cuil;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class PersonalRepository implements IPersonalRepository{

    private final Map<String, Enfermera> data = new HashMap<>();

    @Override
    public Optional<Enfermera> findByCuil(String cuil) {
        return Optional.ofNullable(data.get(cuil));
    }

    @Override
    public void save(Enfermera enfermera) {
        data.put(enfermera.getCuil().getValor(), enfermera);
    }

    @Override
    public void clear() {
        data.clear();
    }

}
