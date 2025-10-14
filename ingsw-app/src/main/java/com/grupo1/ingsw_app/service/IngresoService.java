package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.ColaAtencion;
import com.grupo1.ingsw_app.domain.Ingreso;
import com.grupo1.ingsw_app.domain.NivelEmergencia;
import com.grupo1.ingsw_app.domain.valueobjects.*;
import com.grupo1.ingsw_app.dtos.IngresoRequest;
import com.grupo1.ingsw_app.persistance.IIngresoRepository;
import com.grupo1.ingsw_app.persistance.IPacienteRepository;
import com.grupo1.ingsw_app.security.SesionActual;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngresoService {

    private final IIngresoRepository repoIngreso;
    private final IPacienteRepository repoPaciente;
    private final SesionActual sesionActual;
    private final ColaAtencion cola;


    public IngresoService(IIngresoRepository repoIngreso, IPacienteRepository repoPaciente, SesionActual sesionActual) {
        this.repoIngreso = repoIngreso;
        this.repoPaciente = repoPaciente;
        this.sesionActual = sesionActual;
        this.cola = new ColaAtencion();
    }

    public int posicionEnLaCola(String cuilPaciente) {
        return cola.posicionDe(cuilPaciente);

    }

    public Ingreso registrarIngreso(IngresoRequest req) {
        var paciente = repoPaciente.findByCuil(req.getCuilPaciente())
                .orElseThrow(() -> new IllegalArgumentException(
                        "El paciente no existe en el sistema y debe ser registrado antes del ingreso"));

        var enfermera = sesionActual.getEnfermeraActual();
        if (enfermera == null)
            throw new IllegalStateException("No hay enfermera autenticada");

        // --- Validaciones de campos (mensajes según tus escenarios) ---
        if (req.getInforme() == null || req.getInforme().trim().isEmpty())
            throw new IllegalArgumentException("El informe es obligatorio y no puede estar vacío ni contener solo espacios");

        Float temp = req.getTemperatura();
        if (temp == null || temp.isNaN() || temp.isInfinite() || temp < 0)
            throw new IllegalArgumentException("La temperatura debe ser un número válido en grados Celsius");

        Double fc = req.getFrecuenciaCardiaca();
        if (fc == null || fc.isNaN() || fc.isInfinite())
            throw new IllegalArgumentException("La frecuencia cardíaca debe ser un número válido (latidos por minuto)");
        if (fc < 0)
            throw new IllegalArgumentException("La frecuencia cardíaca no puede ser negativa");

        Double fr = req.getFrecuenciaRespiratoria();
        if (fr == null || fr.isNaN() || fr.isInfinite())
            throw new IllegalArgumentException("La frecuencia respiratoria debe ser un número válido (respiraciones por minuto)");
        if (fr < 0)
            throw new IllegalArgumentException("La frecuencia respiratoria no puede ser negativa");

        Double sist = req.getFrecuenciaSistolica();
        Double diast = req.getFrecuenciaDiastolica();
        if (sist == null || diast == null || sist.isNaN() || diast.isNaN()
                || sist.isInfinite() || diast.isInfinite())
            throw new IllegalArgumentException("La presión arterial debe tener valores numéricos válidos para sistólica y diastólica");
        if (sist < 0 || diast < 0)
            throw new IllegalArgumentException("La presión arterial no puede ser negativa");

        Integer nivelNumero = req.getNivel();
        if (nivelNumero == null || nivelNumero < 1 || nivelNumero > 5)
            throw new IllegalArgumentException("La prioridad ingresada no existe o es nula");

        var nivel = NivelEmergencia.fromNumero(nivelNumero);

        Ingreso ingreso = new Ingreso(paciente, enfermera, nivel);
        ingreso.setDescripcion(req.getInforme());
        ingreso.setTemperatura(new Temperatura(req.getTemperatura()));
        ingreso.setFrecuenciaCardiaca(new FrecuenciaCardiaca(req.getFrecuenciaCardiaca()));
        ingreso.setFrecuenciaRespiratoria(new FrecuenciaRespiratoria(req.getFrecuenciaRespiratoria()));
        ingreso.setTensionArterial(new TensionArterial(
                new Frecuencia(req.getFrecuenciaSistolica()),
                new Frecuencia(req.getFrecuenciaDiastolica())
        ));

        repoIngreso.save(ingreso);
        cola.encolar(ingreso);
        return ingreso;
    }

    public ColaAtencion obtenerCola () {
        cola.limpiar();
        List<Ingreso> ingresos = repoIngreso.findByEstado("PENDIENTE");

        for(Ingreso ingreso: ingresos){
            cola.encolar(ingreso);
        }

        return cola;
    }


}
