package com.alex.probabilidades.estadisticas;

/**
 * Resumen estadístico de un equipo calculado a partir de su histórico de partidos.
 */
public record EstadisticasEquipo(
        String equipo,
        int partidosJugados,
        int victorias,
        int empates,
        int derrotas,
        int golesFavor,
        int golesContra,
        double promedioGolesFavor,
        double promedioGolesContra,
        String racha,
        String ultimosResultados
) {

    public double promedioPuntosPorPartido() {
        int puntos = victorias * 3 + empates;
        return partidosJugados == 0 ? 0.0 : (double) puntos / partidosJugados;
    }
}
