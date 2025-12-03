package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.*;
import com.grupo1.ingsw_app.domain.valueobjects.*;
import com.grupo1.ingsw_app.dtos.IngresoDetalleDTO;
import com.grupo1.ingsw_app.dtos.IngresoRequest;
import com.grupo1.ingsw_app.domain.ColaItem;
import com.grupo1.ingsw_app.dtos.ResumenColaDTO;
import com.grupo1.ingsw_app.exception.CampoInvalidoException;
import com.grupo1.ingsw_app.exception.EntidadNoEncontradaException;
import com.grupo1.ingsw_app.persistence.IIngresoRepository;
import com.grupo1.ingsw_app.persistence.IPacienteRepository;
import com.grupo1.ingsw_app.security.Sesion;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class IngresoService {

    private final IIngresoRepository repoIngreso;
    private final IPacienteRepository repoPaciente;
    private final Sesion sesionActual;
    private final ColaAtencion cola;


    public IngresoService(IIngresoRepository repoIngreso, IPacienteRepository repoPaciente, Sesion sesionActual) {
        this.repoIngreso = repoIngreso;
        this.repoPaciente = repoPaciente;
        this.sesionActual = sesionActual;
        this.cola = new ColaAtencion();

    }

    public Ingreso registrarIngreso(IngresoRequest req) {

        if (req.getInforme() == null || req.getInforme().trim().isEmpty()) {
            throw new CampoInvalidoException("informe", "no puede estar vacío ni contener solo espacios");
        }

        var paciente = repoPaciente.findByCuil(req.getCuilPaciente())
                .orElseThrow(() -> new EntidadNoEncontradaException("paciente", "CUIL: " + req.getCuilPaciente()));

        var enfermera = sesionActual.getEnfermera();

        var nivel = NivelEmergencia.fromNumero(req.getNivel());
        String informe = req.getInforme();

        Ingreso ingreso = new Ingreso(paciente, enfermera, nivel);
        ingreso.setDescripcion(informe);
        ingreso.setTemperatura(new Temperatura(req.getTemperatura()));
        ingreso.setFrecuenciaCardiaca(new FrecuenciaCardiaca(req.getFrecuenciaCardiaca()));
        ingreso.setFrecuenciaRespiratoria(new FrecuenciaRespiratoria(req.getFrecuenciaRespiratoria()));
        ingreso.setTensionArterial(new TensionArterial(
                (req.getFrecuenciaSistolica()),
                (req.getFrecuenciaDiastolica())
        ));

        ingreso.setEstadoIngreso(EstadoIngreso.PENDIENTE);
        ingreso.setFechaIngreso(LocalDateTime.now());

        repoIngreso.save(ingreso);

        return ingreso;
    }

    public ResumenColaDTO obtenerResumenCola() {
        int pendientes  = repoIngreso.countByEstado(EstadoIngreso.PENDIENTE);
        int enAtencion  = repoIngreso.countByEstado(EstadoIngreso.EN_PROCESO);
        int finalizados = repoIngreso.countByEstado(EstadoIngreso.FINALIZADO);

        return new ResumenColaDTO(pendientes, enAtencion, finalizados);
    }

    public List<ColaItem> obtenerColaPendienteDTO() {
        List<Ingreso> pendientes = repoIngreso.findByEstado(EstadoIngreso.PENDIENTE);

        ColaAtencion cola = new ColaAtencion();


        for (Ingreso ingreso : pendientes) {

            String cuil = ingreso.getPaciente().getCuil().getValor();
            Optional<Paciente> paciente = repoPaciente.findByCuil(cuil);

            ColaItem item = new ColaItem(
                    ingreso.getId(),                                     // idIngreso
                    paciente.get().getNombre(),                                // nombrePaciente
                    paciente.get().getApellido(),                              // apellidoPaciente
                    cuil,                       // cuilPaciente
                    ingreso.getNivelEmergencia().getNumero(),           // numero del nivel
                    ingreso.getEstadoIngreso().name(),                  // estado
                    ingreso.getNivelEmergencia().name(),                // nombre del nivel de emergencia
                    ingreso.getFechaIngreso()                            // fechaIngreso
            );

            cola.agregar(item);
        }

        return cola.verCola();
    }

    public IngresoDetalleDTO obtenerDetalle(UUID ingresoId) {
        Ingreso ingreso = repoIngreso.findById(ingresoId)
                .orElseThrow(() -> new EntidadNoEncontradaException("ingreso", ingresoId.toString()));

        // Paciente completo
        String cuilPac = null;
        String nombrePac = null;
        String apellidoPac = null;
        String obraSocialNombre = null;
        String numeroAfiliado = null;

        if (ingreso.getPaciente() != null && ingreso.getPaciente().getCuil() != null) {
            cuilPac = ingreso.getPaciente().getCuil().getValor();

            var pacOpt = repoPaciente.findByCuil(cuilPac);
            if (pacOpt.isPresent()) {
                Paciente p = pacOpt.get();
                nombrePac = p.getNombre();
                apellidoPac = p.getApellido();
                if (p.getAfiliado() != null) {
                    var afi = p.getAfiliado();
                    if (afi.getObraSocial() != null) {
                        obraSocialNombre = afi.getObraSocial().getNombre();
                    }
                    numeroAfiliado = afi.getNumeroAfiliado();
                }
            }
        }

        // Enfermera desde el Ingreso (ya hidratada por el mapper)
        String cuilEnf = null;
        String nombreEnf = null;
        String apellidoEnf = null;

        if (ingreso.getEnfermera() != null) {
            Enfermera e = ingreso.getEnfermera();
            if (e.getCuil() != null) {
                cuilEnf = e.getCuil().getValor();
            }
            nombreEnf = e.getNombre();
            apellidoEnf = e.getApellido();
        }

        var nivel = ingreso.getNivelEmergencia();
        Integer nivelNum = (nivel != null) ? nivel.getNumero() : null;
        String nombreNivel = (nivel != null && nivel.getNivel() != null)
                ? nivel.getNombreEnum()
                : null;

        String estado = ingreso.getEstadoIngreso() != null
                ? ingreso.getEstadoIngreso().name()
                : null;

        Float temp = ingreso.getTemperatura() != null
                ? (Float) ingreso.getTemperatura().getTemperatura()
                : null;

        Double fc = ingreso.getFrecuenciaCardiaca() != null
                ? ingreso.getFrecuenciaCardiaca().getValor()
                : null;

        Double fr = ingreso.getFrecuenciaRespiratoria() != null
                ? ingreso.getFrecuenciaRespiratoria().getValor()
                : null;

        Double sis = null;
        Double dia = null;
        if (ingreso.getTensionArterial() != null) {
            sis = ingreso.getTensionArterial().getSistolica().getValor();
            dia = ingreso.getTensionArterial().getDiastolica().getValor();
        }

        return new IngresoDetalleDTO(
                ingreso.getId(),
                cuilPac,
                nombrePac,
                apellidoPac,
                obraSocialNombre,
                numeroAfiliado,
                cuilEnf,
                nombreEnf,
                apellidoEnf,
                nivelNum,
                nombreNivel,
                estado,
                ingreso.getFechaIngreso(),
                ingreso.getDescripcion(),
                temp,
                fc,
                fr,
                sis,
                dia
        );
    }
}
