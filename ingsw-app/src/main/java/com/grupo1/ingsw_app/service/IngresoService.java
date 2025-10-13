package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.ColaAtencion;
import com.grupo1.ingsw_app.domain.Ingreso;
import com.grupo1.ingsw_app.persistance.IIngresoRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IngresoService {

    private final IIngresoRepository repo;

    public IngresoService(IIngresoRepository repo) {
        this.repo = repo;
    }

    public void limpiarIngresos() {
        repo.clear();
    }

    public int posicionEnLaCola(String cuilPaciente) {
        List<Ingreso> ingresosPendientes = repo.findByEstado("PENDIENTE");
        ColaAtencion colaAtencion = new ColaAtencion();

        for(Ingreso ingreso : ingresosPendientes) {
            colaAtencion.agregar(ingreso);
        }

        return colaAtencion.posicionDe(cuilPaciente);
    }

    public void registrarIngreso(Ingreso ingreso) {
        repo.save(ingreso);
    }
}
