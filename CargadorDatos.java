package com.alex.probabilidades.datos;

import com.alex.probabilidades.modelo.Partido;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Carga el histórico de resultados de fútbol desde un archivo CSV o JSON
 * y lo convierte en una lista de {@link Partido}.
 *
 * Formato CSV esperado (con cabecera):
 *   fecha,equipo_local,equipo_visitante,goles_local,goles_visitante
 *   2024-03-10,Real Madrid,Barcelona,2,1
 *
 * Formato JSON esperado: un array de objetos con las mismas claves.
 */
public class CargadorDatos {

    private final ObjectMapper mapperJson;

    public CargadorDatos() {
        this.mapperJson = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public List<Partido> cargarDesdeCsv(Path ruta) {
        List<String> lineas;
        try {
            lineas = Files.readAllLines(ruta);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo CSV: " + ruta, e);
        }

        if (lineas.isEmpty()) {
            throw new DatosInvalidosException("El archivo CSV está vacío: " + ruta);
        }

        List<Partido> partidos = new ArrayList<>();
        // La primera línea es la cabecera, se ignora.
        for (int i = 1; i < lineas.size(); i++) {
            String linea = lineas.get(i).trim();
            if (linea.isEmpty()) {
                continue;
            }
            partidos.add(parsearLineaCsv(linea, i + 1));
        }

        if (partidos.isEmpty()) {
            throw new DatosInvalidosException("El archivo CSV no contiene ningún partido: " + ruta);
        }

        return partidos;
    }

    private Partido parsearLineaCsv(String linea, int numeroLinea) {
        String[] columnas = linea.split(",");
        if (columnas.length != 5) {
            throw new DatosInvalidosException(
                    "Línea " + numeroLinea + " inválida (se esperaban 5 columnas): '" + linea + "'"
            );
        }

        try {
            LocalDate fecha = LocalDate.parse(columnas[0].trim());
            String equipoLocal = columnas[1].trim();
            String equipoVisitante = columnas[2].trim();
            int golesLocal = Integer.parseInt(columnas[3].trim());
            int golesVisitante = Integer.parseInt(columnas[4].trim());

            if (equipoLocal.isEmpty() || equipoVisitante.isEmpty()) {
                throw new DatosInvalidosException("Línea " + numeroLinea + ": el nombre de equipo no puede estar vacío");
            }
            if (golesLocal < 0 || golesVisitante < 0) {
                throw new DatosInvalidosException("Línea " + numeroLinea + ": los goles no pueden ser negativos");
            }

            return new Partido(fecha, equipoLocal, equipoVisitante, golesLocal, golesVisitante);
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new DatosInvalidosException("Línea " + numeroLinea + " inválida: '" + linea + "'", e);
        }
    }

    public List<Partido> cargarDesdeJson(Path ruta) {
        try {
            Partido[] partidos = mapperJson.readValue(ruta.toFile(), Partido[].class);
            if (partidos.length == 0) {
                throw new DatosInvalidosException("El archivo JSON no contiene ningún partido: " + ruta);
            }
            return List.of(partidos);
        } catch (IOException e) {
            throw new DatosInvalidosException("No se pudo leer o interpretar el archivo JSON: " + ruta, e);
        }
    }

    /** Detecta el formato por la extensión del archivo y carga los datos con el método adecuado. */
    public List<Partido> cargar(Path ruta) {
        String nombre = ruta.getFileName().toString().toLowerCase();
        if (nombre.endsWith(".csv")) {
            return cargarDesdeCsv(ruta);
        } else if (nombre.endsWith(".json")) {
            return cargarDesdeJson(ruta);
        }
        throw new DatosInvalidosException("Formato de archivo no soportado (usa .csv o .json): " + ruta);
    }
}
