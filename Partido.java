package com.alex.probabilidades.modelo;

import java.time.LocalDate;

/**
 * Representa un partido de fútbol ya disputado, con su resultado final.
 * Es el dato de entrada crudo a partir del cual se calculan todas las estadísticas
 * y probabilidades del motor.
 */
public record Partido(
        LocalDate fecha,
        String equipoLocal,
        String equipoVisitante,
        int golesLocal,
        int golesVisitante
) {

    /** Devuelve true si el partido terminó con victoria del equipo local. */
    public boolean ganoLocal() {
        return golesLocal > golesVisitante;
    }

    /** Devuelve true si el partido terminó en empate. */
    public boolean fueEmpate() {
        return golesLocal == golesVisitante;
    }

    /** Devuelve true si el partido terminó con victoria del equipo visitante. */
    public boolean ganoVisitante() {
        return golesVisitante > golesLocal;
    }

    /** Devuelve true si el equipo dado participó en este partido (como local o visitante). */
    public boolean participa(String equipo) {
        return equipoLocal.equalsIgnoreCase(equipo) || equipoVisitante.equalsIgnoreCase(equipo);
    }
}
