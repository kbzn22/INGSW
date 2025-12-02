package com.grupo1.ingsw_app.exception;

import com.grupo1.ingsw_app.api.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ResponseEntity<ApiError> handlePacienteNoEncontrado(
            EntidadNoEncontradaException ex,
            HttpServletRequest request
    ) {
        return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request, "PACIENTE_NO_ENCONTRADO");
    }

    @ExceptionHandler(CampoInvalidoException.class)
    public ResponseEntity<ApiError> handleCampoInvalido(
            CampoInvalidoException ex,
            HttpServletRequest request
    ) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request, "CAMPO_INVALIDO");
    }

    @ExceptionHandler(AfiliacionInvalidaException.class)
    public ResponseEntity<ApiError> handleAfiliacionInvalida(
            AfiliacionInvalidaException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.BAD_REQUEST,           // 🔹 error de negocio, no del servidor
                "Bad Request",
                ex.getMessage(),
                request,
                "AFILIACION_INVALIDA"              // 🔹 código de error interno coherente con tu estilo
        );
    }

    // ======= Captura general =======

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(
            Exception ex,
            HttpServletRequest request
    ) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Ocurrió un error inesperado en el servidor.", request, "ERROR_INTERNO");
    }

    // ======= Body malformado / JSON inválido =======

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers,
            HttpStatusCode status, org.springframework.web.context.request.WebRequest webRequest
    ) {
        HttpServletRequest request = (HttpServletRequest) webRequest.resolveReference(org.springframework.web.context.request.RequestAttributes.REFERENCE_REQUEST);
        ApiError body = new ApiError(
                now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Cuerpo de la solicitud inválido o mal formado.",
                pathOf(request),
                "HTTP_MESSAGE_NOT_READABLE"
        );

        return ResponseEntity.badRequest().body(body);
    }

    // ======= Helpers =======

    private ResponseEntity<ApiError> build(HttpStatus status, String error, String message,
                                           HttpServletRequest request, String code) {
        ApiError body = new ApiError(now(), status.value(), error, message, pathOf(request), code);
        return ResponseEntity.status(status).body(body);
    }

    private static OffsetDateTime now() {
        return OffsetDateTime.now();
    }

    private static String pathOf(HttpServletRequest request) {
        return request != null ? request.getRequestURI() : null;
    }
}