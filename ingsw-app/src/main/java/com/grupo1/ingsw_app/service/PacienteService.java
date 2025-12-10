package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.ObraSocial;
import com.grupo1.ingsw_app.domain.Paciente;
import com.grupo1.ingsw_app.dtos.PacienteRequest;
import com.grupo1.ingsw_app.exception.AfiliacionInvalidaException;
import com.grupo1.ingsw_app.exception.AfiliadoUtilizadoException;
import com.grupo1.ingsw_app.exception.EntidadNoEncontradaException;
import com.grupo1.ingsw_app.external.IObraSocialClient;
import org.springframework.stereotype.Service;
import com.grupo1.ingsw_app.persistence.IPacienteRepository;
import com.grupo1.ingsw_app.domain.Paciente;
import com.grupo1.ingsw_app.persistence.IPacienteRepository;
import com.grupo1.ingsw_app.domain.valueobjects.Cuil;

import java.util.Optional;
import java.util.UUID;

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

        UUID idObraSocial = req.getIdObraSocial();
        String numeroAfiliado = req.getNumeroAfiliado();

        // Caso 2: sin obra social → permitido
        if (idObraSocial == null && numeroAfiliado == null) return null;

        //Caso 3: obra social no existe → rechazado
        ObraSocial obraSocial = obraSocialClient.buscarPorId(idObraSocial);
        if (obraSocial == null) {
            throw new EntidadNoEncontradaException("ObraSocial", "id: "+req.getIdObraSocial());
        }

        //Caso 4: obra social existe pero no tiene un afiliado con ese numero → rechazado
        boolean estaAfiliado = obraSocialClient.estaAfiliado(idObraSocial, numeroAfiliado);
        if (!estaAfiliado) {
            throw new AfiliacionInvalidaException(idObraSocial, numeroAfiliado);
        }


        //Caso 5: obra social y afiliado existen, pero el afiliado ya lo tiene otra persona → rechazado
        boolean yaEstaRegistrado = repo.existsByObraSocialAndNumero(idObraSocial, numeroAfiliado);
        if (yaEstaRegistrado) {
            throw new AfiliadoUtilizadoException(numeroAfiliado);
        }


        // Caso 1: con obra social → permitido
        return obraSocial;

    }
}
