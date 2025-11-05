package com.grupo1.ingsw_app.controller.helpers;

import com.grupo1.ingsw_app.exception.CampoInvalidoException;

public class RequestParser {

    private RequestParser() {} // evita instanciación

    public static String asString(Object v, String campo, String mensaje) {
        if (v == null)
            throw new CampoInvalidoException(campo, mensaje);

        String str = String.valueOf(v).trim();
        if (str.isEmpty())
            throw new CampoInvalidoException(campo, mensaje);

        return str;
    }

    public static Integer parseInteger(Object v, String campo, String mensaje) {
        if (v == null) return null;
        try {
            if (v instanceof Number n) return n.intValue();
            return Integer.parseInt(String.valueOf(v).trim());
        } catch (Exception e) {
            throw new CampoInvalidoException(campo, mensaje);
        }
    }

    public static Long parseLong(Object v, String campo, String mensaje) {
        if (v == null) return null;
        try {
            if (v instanceof Number n) return n.longValue();
            return Long.parseLong(String.valueOf(v).trim());
        } catch (Exception e) {
            throw new CampoInvalidoException(campo, mensaje);
        }
    }

    public static Float parseFloat(Object v, String campo, String mensaje) {
        if (v == null) return null;
        try {
            if (v instanceof Number n) return n.floatValue();
            return Float.parseFloat(String.valueOf(v).trim());
        } catch (Exception e) {
            throw new CampoInvalidoException(campo, mensaje);
        }
    }

    public static Double parseDouble(Object v, String campo, String mensaje) {
        if (v == null) return null;
        try {
            if (v instanceof Number n) return n.doubleValue();
            return Double.parseDouble(String.valueOf(v).trim());
        } catch (Exception e) {
            throw new CampoInvalidoException(campo, mensaje);
        }
    }
}
