package org.example.app;

import org.example.app.interfaces.RepositorioPacientes;
import org.example.domain.Enfermera;
import org.example.domain.Ingreso;
import org.example.domain.NivelEmergencia;
import org.example.domain.Paciente;

import java.util.*;

public class ServicioUrgencias {

    private RepositorioPacientes dbPacientes;

    private final List<Ingreso> listaEspera;

    private static final Map<NivelEmergencia, Integer> RANK = Map.of(
            // mayor prioridad = número más alto (puede ser al revés si preferís)
            NivelEmergencia.CRITICA,       5,
            NivelEmergencia.EMERGENCIA,    4,
            NivelEmergencia.URGENCIA,      3,
            NivelEmergencia.URGENCIA_MENOR,2,
            NivelEmergencia.SIN_URGENCIA,  1
    );

    private static final Comparator<Ingreso> PRIORIDAD = Comparator
            // primero por nivel (descendente: más grande primero)
            .comparingInt((Ingreso i) -> RANK.getOrDefault(i.getNivelEmergencia(), 0))
            .reversed()
            // luego por fecha (ascendente: el que llegó antes va primero)
            .thenComparing(Ingreso::getFechaIngreso);

    public ServicioUrgencias(RepositorioPacientes dbPacientes) {
        this.dbPacientes = dbPacientes;
        this.listaEspera = new ArrayList<>();
    }
    public void registrarUrgencias(String cuilPaciente,
                                    Enfermera enfermera,
                                    String informe,
                                    NivelEmergencia emergencia,
                                    Float temperatura,
                                    Float frecuenciaCardiaca,
                                    Float frecuenciaRespiratoria,
                                    Float frecuenciaSistolica,
                                    Float frecuenciaDiastolica) {

        if (informe != null) informe = informe.trim();
        // buscar paciente
        Optional<Paciente> maybe = dbPacientes.buscarPacientePorCuil(cuilPaciente);

        // si no existe, crearlo y persistirlo
        Paciente paciente = maybe.orElseGet(() -> {
            Paciente nuevo = new Paciente(cuilPaciente, "", "", null); // nombre/apellido/obraSocial placeholders
            dbPacientes.guardarPaciente(nuevo); // <-- REQUIERE este método en tu repositorio
            return nuevo;
        });

        // 2) VALIDACIONES MANDATORIAS (mensajes EXACTOS)
        if (informe == null || informe.isBlank()) {
            throw new IllegalArgumentException("El campo 'informe' es obligatorio");
        }
        if (emergencia == null) {
            throw new IllegalArgumentException("El campo 'nivelEmergencia' es obligatorio");
        }
        if (frecuenciaCardiaca == null) {
            throw new IllegalArgumentException("El campo 'frecuenciaCardiaca' es obligatorio");
        }
        if (frecuenciaRespiratoria == null) {
            throw new IllegalArgumentException("El campo 'frecuenciaRespiratoria' es obligatorio");
        }
        if (frecuenciaSistolica == null || frecuenciaDiastolica == null) {
            throw new IllegalArgumentException("El campo 'tensionArterial' es obligatorio");
        }

        // (los no-negativos los validamos en el escenario siguiente; si querés ya dejarlo listo:)
        if (frecuenciaCardiaca < 0) {
            throw new IllegalArgumentException("La frecuencia cardiaca no puede ser negativa");
        }
        if (frecuenciaRespiratoria < 0) {
            throw new IllegalArgumentException("La frecuencia respiratoria no puede ser negativa");
        }

        Ingreso ingreso = new Ingreso(
                paciente,
                enfermera,
                informe,
                emergencia,
                temperatura,
                frecuenciaCardiaca,
                frecuenciaRespiratoria,
                frecuenciaSistolica,
                frecuenciaDiastolica);

        listaEspera.add(ingreso);
        listaEspera.sort(PRIORIDAD);
    }
    public List<Ingreso> obtenerIngresosPendientes(){
        return this.listaEspera;
    }
    public void limpiar() {
        this.listaEspera.clear();
    }
}
