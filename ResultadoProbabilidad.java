package com.alex.probabilidades.probabilidad;

/**
 * Resultado de simular un futuro encuentro: la probabilidad de cada desenlace
 * (victoria local, empate, victoria visitante) y su cuota decimal equivalente
 * (cuota = 1 / probabilidad).
 */
public record ResultadoProbabilidad(
        String equipoLocal,
        String equipoVisitante,
        double golesEsperadosLocal,
        double golesEsperadosVisitante,
        double probabilidadLocal,
        double probabilidadEmpate,
        double probabilidadVisitante,
        double cuotaLocal,
        double cuotaEmpate,
        double cuotaVisitante
) {

    @Override
    public String toString() {
        return String.format(
                "%s vs %s%n" +
                "  Goles esperados: %.2f - %.2f%n" +
                "  P(Local)=%.1f%% (cuota %.2f)  P(Empate)=%.1f%% (cuota %.2f)  P(Visitante)=%.1f%% (cuota %.2f)",
                equipoLocal, equipoVisitante,
                golesEsperadosLocal, golesEsperadosVisitante,
                probabilidadLocal * 100, cuotaLocal,
                probabilidadEmpate * 100, cuotaEmpate,
                probabilidadVisitante * 100, cuotaVisitante
        );
    }
}
