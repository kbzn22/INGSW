// src/main/java/com/grupo1/ingsw_app/service/AdminLogService.java
package com.grupo1.ingsw_app.service;

import com.grupo1.ingsw_app.domain.Atencion;
import com.grupo1.ingsw_app.domain.Ingreso;
import com.grupo1.ingsw_app.dtos.AtencionLogDTO;
import com.grupo1.ingsw_app.dtos.AtencionResumenDTO;
import com.grupo1.ingsw_app.exception.EntidadNoEncontradaException;
import com.grupo1.ingsw_app.persistence.IAtencionRepository;
import com.grupo1.ingsw_app.persistence.IIngresoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class AdminLogService {

    private final IIngresoRepository ingresoRepo;
    private final IAtencionRepository atencionRepo;

    public AdminLogService(IIngresoRepository ingresoRepo,
                           IAtencionRepository atencionRepo) {
        this.ingresoRepo = ingresoRepo;
        this.atencionRepo = atencionRepo;
    }

    // -------- helpers fechas --------
    private LocalDateTime toStart(LocalDate d) {
        return d != null ? d.atStartOfDay() : null;
    }

    private LocalDateTime toEnd(LocalDate d) {
        return d != null ? d.atTime(LocalTime.MAX) : null;
    }

    // -------- Ingresos --------
    public byte[] exportIngresos(LocalDate desde,
                                 LocalDate hasta,
                                 String cuilPaciente,
                                 String cuilEnfermera) {

        LocalDateTime d = toStart(desde);
        LocalDateTime h = toEnd(hasta);
        System.out.println(cuilPaciente);
        List<Ingreso> ingresos = ingresoRepo.findForLog(d, h, cuilPaciente, cuilEnfermera);
        System.out.println("Cantidad de ingresos: " + ingresos.size());
        for (Ingreso ing : ingresos) {
            System.out.println("Ingreso ID = " + ing.getId()
                    + " | pacienteCuil=" + (ing.getPaciente() != null ? ing.getPaciente().getCuil().getValor() : "null")
                    + " | enfermeraCuil=" + (ing.getEnfermera() != null ? ing.getEnfermera().getCuil().getValor() : "null")
                    + " | nivel=" + (ing.getNivelEmergencia() != null ? ing.getNivelEmergencia().getNumero() : null)
                    + " | estado=" + (ing.getEstadoIngreso() != null ? ing.getEstadoIngreso().name() : null)
                    + " | fechaIngreso=" + ing.getFechaIngreso()
            );
        }
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Ingresos");
            int rowIdx = 0;

            // header
            Row header = sheet.createRow(rowIdx++);
            String[] cols = {
                    "ID", "CUIL Paciente", "CUIL Enfermera",
                    "Nivel", "Estado", "Fecha ingreso",
                    "Temperatura", "Frec. cardiaca", "Frec. respiratoria",
                    "Sistólica", "Diastólica", "Informe"
            };
            for (int i = 0; i < cols.length; i++) {
                header.createCell(i).setCellValue(cols[i]);
            }

            // datos
            for (Ingreso ing : ingresos) {
                Row r = sheet.createRow(rowIdx++);
                int c = 0;
                r.createCell(c++).setCellValue(ing.getId().toString());
                r.createCell(c++).setCellValue(
                        ing.getPaciente() != null && ing.getPaciente().getCuil() != null
                                ? ing.getPaciente().getCuil().getValor()
                                : ""
                );
                r.createCell(c++).setCellValue(
                        ing.getEnfermera() != null && ing.getEnfermera().getCuil() != null
                                ? ing.getEnfermera().getCuil().getValor()
                                : ""
                );
                r.createCell(c++).setCellValue(
                        ing.getNivelEmergencia() != null ? ing.getNivelEmergencia().getNumero() : 0
                );
                r.createCell(c++).setCellValue(
                        ing.getEstadoIngreso() != null ? ing.getEstadoIngreso().name() : ""
                );
                r.createCell(c++).setCellValue(
                        ing.getFechaIngreso() != null ? ing.getFechaIngreso().toString() : ""
                );
                r.createCell(c++).setCellValue(
                        ing.getTemperatura() != null ? ing.getTemperatura().getTemperatura() : 0
                );
                r.createCell(c++).setCellValue(
                        ing.getFrecuenciaCardiaca() != null ? ing.getFrecuenciaCardiaca().getValor() : 0
                );
                r.createCell(c++).setCellValue(
                        ing.getFrecuenciaRespiratoria() != null ? ing.getFrecuenciaRespiratoria().getValor() : 0
                );
                r.createCell(c++).setCellValue(
                        ing.getTensionArterial() != null && ing.getTensionArterial().getSistolica() != null
                                ? ing.getTensionArterial().getSistolica().getValor()
                                : 0
                );
                r.createCell(c++).setCellValue(
                        ing.getTensionArterial() != null && ing.getTensionArterial().getDiastolica() != null
                                ? ing.getTensionArterial().getDiastolica().getValor()
                                : 0
                );
                r.createCell(c).setCellValue(ing.getDescripcion() != null ? ing.getDescripcion() : "");
            }

            for (int i = 0; i < cols.length; i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generando Excel de ingresos", e);
        }
    }


    public byte[] exportAtenciones(LocalDate desde,
                                   LocalDate hasta,
                                   String cuilDoctor) {

        LocalDateTime d = toStart(desde);
        LocalDateTime h = toEnd(hasta);

        List<Atencion> atenciones = atencionRepo.findForLog(d, h, cuilDoctor);

        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Atenciones");
            int rowIdx = 0;

            Row header = sheet.createRow(rowIdx++);
            String[] cols = {
                    "ID Atención", "Ingreso ID", "CUIL Doctor",
                    "Fecha atención", "Informe"
            };
            for (int i = 0; i < cols.length; i++) {
                header.createCell(i).setCellValue(cols[i]);
            }

            for (Atencion a : atenciones) {
                Row r = sheet.createRow(rowIdx++);
                int c = 0;
                r.createCell(c++).setCellValue(a.getId().toString());
                r.createCell(c++).setCellValue(
                        a.getIngreso() != null && a.getIngreso().getId() != null
                                ? a.getIngreso().getId().toString()
                                : ""
                );
                r.createCell(c++).setCellValue(
                        a.getDoctor() != null && a.getDoctor().getCuil() != null
                                ? a.getDoctor().getCuil().getValor()
                                : ""
                );
                r.createCell(c++).setCellValue(
                        a.getFechaAtencion() != null ? a.getFechaAtencion().toString() : ""
                );
                r.createCell(c).setCellValue(a.getInforme() != null ? a.getInforme() : "");
            }

            for (int i = 0; i < cols.length; i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error generando Excel de atenciones", e);
        }
    }
    public AtencionLogDTO getAtencionDetalle(UUID idAtencion) {

        var atencion = atencionRepo.findById(idAtencion)
                .orElseThrow(() ->
                        new EntidadNoEncontradaException("atencion", idAtencion.toString()));

        // Traer el ingreso COMPLETO
        var ingreso = ingresoRepo.findById(atencion.getIngreso().getId())
                .orElseThrow(() ->
                        new EntidadNoEncontradaException("ingreso", atencion.getIngreso().getId().toString()));

        // Extraer datos seguros
        String cuilDoctor = atencion.getDoctor() != null && atencion.getDoctor().getCuil() != null
                ? atencion.getDoctor().getCuil().getValor()
                : null;

        String cuilPaciente = ingreso.getPaciente() != null && ingreso.getPaciente().getCuil() != null
                ? ingreso.getPaciente().getCuil().getValor()
                : null;

        String cuilEnfermera = ingreso.getEnfermera() != null && ingreso.getEnfermera().getCuil() != null
                ? ingreso.getEnfermera().getCuil().getValor()
                : null;

        Integer nivel = ingreso.getNivelEmergencia() != null
                ? ingreso.getNivelEmergencia().getNumero()
                : null;

        String estado = ingreso.getEstadoIngreso() != null
                ? ingreso.getEstadoIngreso().name()
                : null;

        LocalDateTime fechaIngreso = ingreso.getFechaIngreso();

        // Ahora SÍ coincide con el constructor real
        return new AtencionLogDTO(
                atencion.getId(),
                ingreso.getId(),
                cuilDoctor,
                atencion.getInforme(),
                atencion.getFechaAtencion(),
                cuilPaciente,
                cuilEnfermera,
                nivel,
                estado,
                fechaIngreso
        );
    }
}
