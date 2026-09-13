package com.alex.probabilidades.datos;

/**
 * Se lanza cuando un archivo de entrada (CSV o JSON) contiene datos
 * mal formados: columnas que faltan, fechas o números inválidos, etc.
 */
public class DatosInvalidosException extends RuntimeException {

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }

    public DatosInvalidosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
