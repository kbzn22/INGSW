package com.grupo1.ingsw_app.controller.helpers;

import com.grupo1.ingsw_app.exception.CampoInvalidoException;

import java.util.UUID;

public class RequestParser {

    private RequestParser() {} // evita instanciación

    public static String asString(Object v, String campo, String mensaje) {
        if (v == null) throw new CampoInvalidoException(campo, mensaje);

        String str = String.valueOf(v).trim();
        if (str.isEmpty())
            throw new CampoInvalidoException(campo, mensaje);

        return str;
    }

    public static Integer parseInteger(Object v, String campo, String mensaje) {
        if (v == null) return null;
        try {
            if (v instanceof Integer i) return i;
            if (v instanceof Long l) return Math.toIntExact(l);
            if (v instanceof Double d) {
                if (d % 1 != 0) throw new CampoInvalidoException(campo, mensaje);
                return d.intValue();
            }
            if (v instanceof Float f) {
                if (f % 1 != 0) throw new CampoInvalidoException(campo, mensaje);
                return f.intValue();
            }
            return Integer.parseInt(String.valueOf(v).trim());
        } catch (Exception e) {
            throw new CampoInvalidoException(campo, mensaje);
        }
    }


    public static UUID parseUUID(Object v, String campo, String mensaje) {
        if (v == null) return null;
        try {
            if (v instanceof UUID uuid) return uuid;
            return UUID.fromString(String.valueOf(v).trim());
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
