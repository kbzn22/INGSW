package com.grupo1.ingsw_app.domain;

import java.util.ArrayList;
import java.util.List;

public class ColaAtencion {

    private List<Ingreso> ingresosEnCola;

    public ColaAtencion() {
        ingresosEnCola = new ArrayList<Ingreso>();
    }
}
