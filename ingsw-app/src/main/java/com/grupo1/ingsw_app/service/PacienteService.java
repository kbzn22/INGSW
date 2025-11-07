package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.ObraSocial;
import com.grupo1.ingsw_app.domain.Paciente;
import com.grupo1.ingsw_app.dtos.PacienteRequest;
import com.grupo1.ingsw_app.exception.AfiliacionInvalidaException;
import com.grupo1.ingsw_app.exception.CampoInvalidoException;
import com.grupo1.ingsw_app.exception.EntidadNoEncontradaException;
import com.grupo1.ingsw_app.external.IObraSocialClient;
import org.springframework.stereotype.Service;
import com.grupo1.ingsw_app.persistance.IPacienteRepository;
import com.grupo1.ingsw_app.domain.valueobjects.Cuil;

import java.util.Optional;

@Service
public class PacienteService {

    private final IPacienteRepository repo;
    private final IObraSocialClient obraSocialClient;

    public PacienteService(IPacienteRepository repo, IObraSocialClient obraSocialClient) {
        this.repo = repo;
        this.obraSocialClient = obraSocialClient;
    }

    public Paciente buscarPorCuil(String cuilString) {

        Cuil cuil = new Cuil(cuilString);

        Optional<Paciente> paciente = repo.findByCuil(cuil.getValor());

        if (paciente.isEmpty()) {
            throw new EntidadNoEncontradaException("paciente", "CUIL: "+cuil.getValor());
        }

        return paciente.get();
    }

    public Paciente registrarPaciente(PacienteRequest req) {
        ObraSocial obraSocial = validarDatosObraSocial(req);
        Paciente paciente = new Paciente(
                req.getCuil(),
                req.getNombre(),
                req.getApellido(),
                req.getEmail(),
                req.getCalle(),
                req.getNumero(),
                req.getLocalidad(),
                obraSocial,
                req.getNumeroAfiliado()
        );
        repo.save(paciente);
        return paciente;
    }

    private ObraSocial validarDatosObraSocial(PacienteRequest req){

        // Caso 1: sin obra social → permitido
        if (req.getIdObraSocial() == null && req.getNumeroAfiliado() == null) return null;

        // Caso 2: uno de los dos falta → rechazado
        if (req.getIdObraSocial() == null || req.getNumeroAfiliado() == null) {
            throw new CampoInvalidoException(
                    "obraSocial/numeroAfiliado",
                    "debe indicarse obra social y número de afiliado juntos"
            );
        }

        //Caso 3: obra social no existe → rechazado
        ObraSocial obraSocial = obraSocialClient.buscarPorId(req.getIdObraSocial());
        if (obraSocial == null) {
            throw new EntidadNoEncontradaException("ObraSocial", "id: "+req.getIdObraSocial());
        }

        //Caso 4: obra social existe pero no tiene un afiliado con ese numero → rechazado
        boolean estaAfiliado = obraSocialClient.estaAfiliado(req.getIdObraSocial(), req.getNumeroAfiliado());
        if (!estaAfiliado) {
            throw new AfiliacionInvalidaException("El número de afiliado no pertenece a la obra social indicada");
        }

        return obraSocial;

    }
}
