package com.grupo1.ingsw_app.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ObraSocial {

    private UUID id;
    private String nombre;
    private List<Afiliado> afiliados=new ArrayList<>();

    public ObraSocial(UUID id, String nombre){
        this.id = id;
        this.nombre = nombre;
    }

    public String getNombre(){
        return this.nombre;
    }
    public UUID getId(){return this.id;}

    public void agregarAfiliado(Afiliado a){
        afiliados.add(a);
    }
    public Afiliado buscarAfiliado(String numeroAfiliado){
        if (numeroAfiliado == null) return null;

        return afiliados.stream()
                .filter(a -> numeroAfiliado.equals(a.getNumeroAfiliado()))
                .findFirst()
                .orElse(null);
    }
}
