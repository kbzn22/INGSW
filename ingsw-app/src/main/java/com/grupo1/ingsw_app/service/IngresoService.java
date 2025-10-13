package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.persistance.IIngresoRepository;
import org.springframework.stereotype.Service;

@Service
public class IngresoService {
    private final IIngresoRepository repo;

    public IngresoService(IIngresoRepository repo) {
        this.repo = repo;
    }

}
