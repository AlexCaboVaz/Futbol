package com.alex.probabilidades.estadisticas;

import com.alex.probabilidades.modelo.Partido;

import java.util.Comparator;
import java.util.List;

/**
 * Calcula estadísticas (victorias, empates, derrotas, promedios de goles, rachas...)
 * a partir del histórico de partidos de un equipo.
 */
public class CalculadoraEstadisticas {

    /** Calcula el resumen estadístico completo de un equipo. */
    public EstadisticasEquipo calcular(List<Partido> partidos, String equipo) {
        List<Partido> partidosEquipo = partidos.stream()
                .filter(p -> p.participa(equipo))
                .sorted(Comparator.comparing(Partido::fecha))
                .toList();

        if (partidosEquipo.isEmpty()) {
            throw new IllegalArgumentException("No hay partidos registrados para el equipo: " + equipo);
        }

        int victorias = 0, empates = 0, derrotas = 0, golesFavor = 0, golesContra = 0;

        for (Partido p : partidosEquipo) {
            boolean esLocal = p.equipoLocal().equalsIgnoreCase(equipo);
            int golesEquipo = esLocal ? p.golesLocal() : p.golesVisitante();
            int golesRival = esLocal ? p.golesVisitante() : p.golesLocal();

            golesFavor += golesEquipo;
            golesContra += golesRival;

            if (golesEquipo > golesRival) {
                victorias++;
            } else if (golesEquipo == golesRival) {
                empates++;
            } else {
                derrotas++;
            }
        }

        int totalPartidos = partidosEquipo.size();
        double promedioGolesFavor = (double) golesFavor / totalPartidos;
        double promedioGolesContra = (double) golesContra / totalPartidos;

        String racha = calcularRacha(partidosEquipo, equipo);
        String ultimosResultados = calcularUltimosResultados(partidosEquipo, equipo, 5);

        return new EstadisticasEquipo(
                equipo, totalPartidos, victorias, empates, derrotas,
                golesFavor, golesContra, promedioGolesFavor, promedioGolesContra,
                racha, ultimosResultados
        );
    }

    /**
     * Calcula la racha actual del equipo: cuántos partidos consecutivos (contando desde el
     * más reciente hacia atrás) ha tenido el mismo resultado. Ejemplos: "3V" (3 victorias
     * seguidas), "2D" (2 derrotas seguidas), "1E" (el último partido fue un empate y el
     * anterior no).
     */
    String calcularRacha(List<Partido> partidosOrdenados, String equipo) {
        char resultadoActual = resultadoDe(partidosOrdenados.get(partidosOrdenados.size() - 1), equipo);
        int longitud = 0;

        for (int i = partidosOrdenados.size() - 1; i >= 0; i--) {
            char resultado = resultadoDe(partidosOrdenados.get(i), equipo);
            if (resultado != resultadoActual) {
                break;
            }
            longitud++;
        }

        return longitud + String.valueOf(resultadoActual);
    }

    /** Devuelve los últimos N resultados como una cadena, por ejemplo "VVEDV" (el más reciente al final). */
    String calcularUltimosResultados(List<Partido> partidosOrdenados, String equipo, int cantidad) {
        StringBuilder sb = new StringBuilder();
        int desde = Math.max(0, partidosOrdenados.size() - cantidad);
        for (int i = desde; i < partidosOrdenados.size(); i++) {
            sb.append(resultadoDe(partidosOrdenados.get(i), equipo));
        }
        return sb.toString();
    }

    private char resultadoDe(Partido p, String equipo) {
        boolean esLocal = p.equipoLocal().equalsIgnoreCase(equipo);
        int golesEquipo = esLocal ? p.golesLocal() : p.golesVisitante();
        int golesRival = esLocal ? p.golesVisitante() : p.golesLocal();

        if (golesEquipo > golesRival) return 'V';
        if (golesEquipo < golesRival) return 'D';
        return 'E';
    }

    /** Promedio de goles marcados por un equipo cuando juega como local. */
    public double promedioGolesMarcadosComoLocal(List<Partido> partidos, String equipo) {
        return promedio(partidos, p -> p.equipoLocal().equalsIgnoreCase(equipo), Partido::golesLocal,
                "El equipo '" + equipo + "' no tiene partidos registrados como local");
    }

    /** Promedio de goles encajados por un equipo cuando juega como local. */
    public double promedioGolesEncajadosComoLocal(List<Partido> partidos, String equipo) {
        return promedio(partidos, p -> p.equipoLocal().equalsIgnoreCase(equipo), Partido::golesVisitante,
                "El equipo '" + equipo + "' no tiene partidos registrados como local");
    }

    /** Promedio de goles marcados por un equipo cuando juega como visitante. */
    public double promedioGolesMarcadosComoVisitante(List<Partido> partidos, String equipo) {
        return promedio(partidos, p -> p.equipoVisitante().equalsIgnoreCase(equipo), Partido::golesVisitante,
                "El equipo '" + equipo + "' no tiene partidos registrados como visitante");
    }

    /** Promedio de goles encajados por un equipo cuando juega como visitante. */
    public double promedioGolesEncajadosComoVisitante(List<Partido> partidos, String equipo) {
        return promedio(partidos, p -> p.equipoVisitante().equalsIgnoreCase(equipo), Partido::golesLocal,
                "El equipo '" + equipo + "' no tiene partidos registrados como visitante");
    }

    /** Promedio total de goles marcados por partido (ambos equipos sumados) en toda la liga cargada. */
    public double promedioGolesPorPartidoLiga(List<Partido> partidos) {
        if (partidos.isEmpty()) {
            throw new IllegalArgumentException("No hay partidos cargados para calcular el promedio de la liga");
        }
        double totalGoles = partidos.stream()
                .mapToInt(p -> p.golesLocal() + p.golesVisitante())
                .sum();
        // Se divide por partido (no por equipo-partido) para usarlo como factor de normalización
        // en el modelo de Poisson: goles totales del partido / 2 = "goles esperados medios de un equipo".
        return totalGoles / (2.0 * partidos.size());
    }

    private double promedio(
            List<Partido> partidos,
            java.util.function.Predicate<Partido> filtro,
            java.util.function.ToIntFunction<Partido> extractorGoles,
            String mensajeError
    ) {
        List<Partido> filtrados = partidos.stream().filter(filtro).toList();
        if (filtrados.isEmpty()) {
            throw new IllegalArgumentException(mensajeError);
        }
        double total = filtrados.stream().mapToInt(extractorGoles).sum();
        return total / filtrados.size();
    }
}
