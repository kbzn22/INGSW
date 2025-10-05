import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import mock.DBPruebaEnMemoria;
import org.example.app.ServicioUrgencias;
import org.example.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;

public class moduloUrgenciasStepDefinitions {

    private Enfermera enfermera;
    private DBPruebaEnMemoria dbMockeada;
    private ServicioUrgencias servicioUrgencias;
    private Exception ultimaExcepcion;

    public moduloUrgenciasStepDefinitions() {
        this.dbMockeada = new DBPruebaEnMemoria();
        this.servicioUrgencias = new ServicioUrgencias(dbMockeada);
    }

    // en tu clase de steps (static helpers)
    private static String cell(Map<String,String> row, String key) {
        return row.entrySet().stream()
                .filter(e -> e.getKey() != null && e.getKey().trim().equalsIgnoreCase(key.trim()))
                .map(Map.Entry::getValue)
                .findFirst().orElse(null);
    }

    private static Float cellFloat(Map<String,String> row, String key) {
        String v = cell(row, key);
        if (v == null || v.isBlank()) return null;
        return Float.parseFloat(v.trim());
    }

    public void resetState() {
        if (dbMockeada != null) dbMockeada.limpiar();  // helper del mock
        if (servicioUrgencias != null) servicioUrgencias.limpiar(); // limpia la cola
        // (opcional) re-instanciar explícitamente:
        // this.servicioUrgencias = new ServicioUrgencias(dbMockeada);
    }

    @Given("que la siguiente enfermera esta registrada:")
    public void queLaSiguienteEnfermeraEstaRegistrada(List<Map<String, String>> tabla) {
        String nombre = tabla.getFirst().get("Nombre");
        String apellido = tabla.getFirst().get("Apellido");

        enfermera = new Enfermera(nombre, apellido);
    }

    @Given("Dado que estan registrados los siguientes pacientes:")
    public void dadoQueEstanRegistradosLosSiguientesPacientes(List<Map<String, String>> tabla) {

        for (Map<String, String> fila : tabla) {
            String cuil = fila.get("Cuil");
            String nombre = fila.get("Nombre");
            String apellido = fila.get("Apellido");
            String obraSocial = fila.get("Obra Social");

            Paciente paciente = new Paciente(cuil, nombre, apellido, obraSocial);
            dbMockeada.guardarPaciente(paciente);
        }
    }

    @When("Ingresa a urgencias el siguiente paciente:")
    public void ingresaAUrgenciasElSiguientePaciente(List<Map<String, String>> tabla) {
        Map<String, String> fila = tabla.getFirst();

        String cuil  = cell(fila, "Cuil");
        String informe = cell(fila, "Informe");
        String nivelTxt = cell(fila, "Nivel de Emergencia");

        Float temperatura = cellFloat(fila, "Temperatura");
        Float fc = cellFloat(fila, "Frecuencia Cardiaca");
        Float fr = cellFloat(fila, "Frecuencia Respiratoria");

        Float sist = null, diast = null;
        String ta = cell(fila, "Tension Arterial");
        if (ta != null && !ta.isBlank()) {
            String[] partes = ta.split("/");
            if (partes.length == 2) {
                sist = Float.parseFloat(partes[0].trim());
                diast = Float.parseFloat(partes[1].trim());
            }
        }

        NivelEmergencia nivelEmergencia = Arrays.stream(NivelEmergencia.values())
                .filter(nivel -> nivel.tieneNombre(nivelTxt)) // tu helper
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nivel desconocido: " + nivelTxt));

        servicioUrgencias.registrarUrgencias(
                cuil, enfermera, informe, nivelEmergencia,
                temperatura, fc, fr, sist, diast
        );
    }

    @Then("La lista de espera esta ordenada por el cuil de la siguiente manera:")
    public void laListaDeEsperaEstaOrdenadaPorElCuilDeLaSiguienteManera(List<String> esperado) {
        List<String> actual = servicioUrgencias.obtenerIngresosPendientes()
                .stream()
                .map(Ingreso::getCuilPaciente)
                .toList();

        // mismo tamaño y MISMO ORDEN
        assertThat(actual).containsExactlyElementsOf(esperado);
    }

    @And("El ingreso del paciente {string} esta en estado {string}")
    public void elIngresoDelPacienteEstaEnEstado(String cuil, String estadoTxt) {
        EstadoIngreso estadoEsperado = EstadoIngreso.valueOf(estadoTxt); // PENDIENTE
        Ingreso ingreso = servicioUrgencias.obtenerIngresosPendientes()
                .stream()
                .filter(i -> i.getCuilPaciente().equals(cuil))
                .findFirst()
                .orElse(null);
        assertThat(ingreso)
                .as("Ingreso no encontrado para cuil " + cuil)
                .isNotNull();
        assertThat(ingreso.getEstado()).isEqualTo(estadoEsperado);
    }

    @And("El ingreso del paciente {string} tiene enfermera {string}")
    public void elIngresoDelPacienteTieneEnfermera(String cuil, String nombreCompleto) {
        Ingreso ingreso = servicioUrgencias.obtenerIngresosPendientes()
                .stream()
                .filter(i -> i.getCuilPaciente().equals(cuil))
                .findFirst()
                .orElse(null);

        assertThat(ingreso)
                .as("Ingreso no encontrado para cuil " + cuil)
                .isNotNull();

        String enfermeraStr = ingreso.getEnfermera().getNombre() + " " + ingreso.getEnfermera().getApellido();
        assertThat(enfermeraStr).isEqualTo(nombreCompleto);
    }

    @Given("No existe aun el paciente con cuil {string}")
    public void noExisteAunElPacienteConCuil(String cuil) {
        dbMockeada.eliminarPacienteSiExiste(cuil); // implementá este helper si no existe
        assertThat(dbMockeada.buscarPacientePorCuil(cuil)).isEmpty();
    }

    @Then("El paciente con cuil {string} fue creado")
    public void elPacienteConCuilFueCreado(String cuil) {
        assertThat(dbMockeada.buscarPacientePorCuil(cuil))
                .as("Se esperaba que el paciente fuera creado")
                .isPresent();
    }

    @When("Intento ingresar a urgencias el siguiente paciente \\(capturando errores):")
    public void intentoIngresarAUrgenciasElSiguientePacienteCapturandoErrores(List<Map<String, String>> tabla) {
        Map<String, String> fila = tabla.getFirst();

        String cuil = getOrNull(fila, "Cuil");
        String informe = getOrNull(fila, "Informe");
        String nivelTxt = getOrNull(fila, "Nivel de Emergencia");
        String tempTxt = getOrNull(fila, "Temperatura");
        String fcTxt = getOrNull(fila, "Frecuencia Cardiaca");
        String frTxt = getOrNull(fila, "Frecuencia Respiratoria");
        String taTxt = getOrNull(fila, "Tension Arterial");

        Float temperatura = parseFloatOrNull(tempTxt);
        Float fc = parseFloatOrNull(fcTxt);
        Float fr = parseFloatOrNull(frTxt);

        Float sist = null, diast = null;
        if (taTxt != null && !taTxt.isBlank()) {
            String[] partes = taTxt.split("/");
            if (partes.length == 2) {
                sist = parseFloatOrNull(partes[0]);
                diast = parseFloatOrNull(partes[1]);
            }
        }

        NivelEmergencia nivel = null;
        if (nivelTxt != null && !nivelTxt.isBlank()) {
            nivel = Arrays.stream(NivelEmergencia.values())
                    .filter(n -> n.tieneNombre(nivelTxt))
                    .findFirst()
                    .orElse(null);
        }

        try {
            servicioUrgencias.registrarUrgencias(
                    cuil, enfermera, informe, nivel,
                    temperatura, fc, fr, sist, diast
            );
            ultimaExcepcion = null;
        } catch (Exception ex) {
            ultimaExcepcion = ex; // <<— capturamos para el Then
        }
    }

    @Then("Veo error con mensaje {string}")
    public void veoErrorConMensaje(String mensaje) {
        assertThat(ultimaExcepcion)
                .as("Se esperaba una excepción")
                .isNotNull();
        assertThat(ultimaExcepcion.getMessage())
                .contains(mensaje);
    }

    // --- helpers simples ---
    private static String getOrNull(Map<String, String> m, String k) {
        return m.containsKey(k) ? m.get(k) : null;
    }

    private static Float parseFloatOrNull(String s) {
        if (s == null || s.isBlank()) return null;
        return Float.parseFloat(s);
    }

    @And("La lista de espera contiene los siguientes ingresos \\(en orden de llegada):")
    public void laListaDeEsperaContieneLosSiguientesIngresosEnOrdenDeLlegada(List<Map<String, String>> tabla) {
        for (Map<String, String> fila : tabla) {
            String cuil = fila.get("Cuil");
            String nivelTxt = fila.get("Nivel de Emergencia");

            // parseo de nivel (usa tu helper real)
            NivelEmergencia nivel = Arrays.stream(NivelEmergencia.values())
                    .filter(n -> n.tieneNombre(nivelTxt))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Nivel desconocido: " + nivelTxt));

            // Registramos entradas con datos razonables para no gatillar validaciones
            servicioUrgencias.registrarUrgencias(
                    cuil, enfermera, "Ingreso previo", nivel,
                    37.0f, 70f, 16f, 120f, 80f
            );

            // No tocamos la fecha: al registrarlos en este orden ya queda la traza temporal correcta
        }
    }
}