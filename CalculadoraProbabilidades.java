package com.alex.probabilidades.probabilidad;

import com.alex.probabilidades.estadisticas.CalculadoraEstadisticas;
import com.alex.probabilidades.modelo.Partido;

import java.util.List;

/**
 * Simula el resultado de un futuro encuentro usando un modelo de distribución de Poisson:
 * a partir del promedio de goles marcados/encajados de cada equipo (como local o visitante)
 * y el promedio de goles de la liga, se estiman los "goles esperados" de cada equipo y se
 * calcula la probabilidad de cada marcador posible para derivar P(local), P(empate) y
 * P(visitante).
 *
 * Es el mismo enfoque (simplificado) que usan muchos modelos reales de casas de apuestas
 * y analítica deportiva.
 */
public class CalculadoraProbabilidades {

    /** Máximo número de goles por equipo que se simula (más de 10 goles es estadísticamente irrelevante). */
    private static final int MAX_GOLES_SIMULADOS = 10;

    private final CalculadoraEstadisticas calculadoraEstadisticas;

    public CalculadoraProbabilidades() {
        this(new CalculadoraEstadisticas());
    }

    public CalculadoraProbabilidades(CalculadoraEstadisticas calculadoraEstadisticas) {
        this.calculadoraEstadisticas = calculadoraEstadisticas;
    }

    public ResultadoProbabilidad calcular(List<Partido> partidos, String equipoLocal, String equipoVisitante) {
        double promedioLiga = calculadoraEstadisticas.promedioGolesPorPartidoLiga(partidos);

        double ataqueLocal = calculadoraEstadisticas.promedioGolesMarcadosComoLocal(partidos, equipoLocal);
        double defensaVisitante = calculadoraEstadisticas.promedioGolesEncajadosComoVisitante(partidos, equipoVisitante);

        double ataqueVisitante = calculadoraEstadisticas.promedioGolesMarcadosComoVisitante(partidos, equipoVisitante);
        double defensaLocal = calculadoraEstadisticas.promedioGolesEncajadosComoLocal(partidos, equipoLocal);

        double golesEsperadosLocal = (ataqueLocal * defensaVisitante) / promedioLiga;
        double golesEsperadosVisitante = (ataqueVisitante * defensaLocal) / promedioLiga;

        double probLocal = 0.0;
        double probEmpate = 0.0;
        double probVisitante = 0.0;

        for (int golesL = 0; golesL <= MAX_GOLES_SIMULADOS; golesL++) {
            for (int golesV = 0; golesV <= MAX_GOLES_SIMULADOS; golesV++) {
                double probabilidadMarcador = poisson(golesEsperadosLocal, golesL) * poisson(golesEsperadosVisitante, golesV);

                if (golesL > golesV) {
                    probLocal += probabilidadMarcador;
                } else if (golesL == golesV) {
                    probEmpate += probabilidadMarcador;
                } else {
                    probVisitante += probabilidadMarcador;
                }
            }
        }

        // Pequeño reajuste para que las tres probabilidades sumen exactamente 1
        // (la suma teórica es ligeramente inferior a 1 al truncar la simulación en MAX_GOLES_SIMULADOS).
        double suma = probLocal + probEmpate + probVisitante;
        probLocal /= suma;
        probEmpate /= suma;
        probVisitante /= suma;

        return new ResultadoProbabilidad(
                equipoLocal, equipoVisitante,
                golesEsperadosLocal, golesEsperadosVisitante,
                probLocal, probEmpate, probVisitante,
                aCuota(probLocal), aCuota(probEmpate), aCuota(probVisitante)
        );
    }

    /** Probabilidad de que una variable de Poisson con media lambda tome el valor k. */
    double poisson(double lambda, int k) {
        return Math.exp(-lambda) * Math.pow(lambda, k) / factorial(k);
    }

    private long factorial(int n) {
        long resultado = 1;
        for (int i = 2; i <= n; i++) {
            resultado *= i;
        }
        return resultado;
    }

    /** Convierte una probabilidad (0-1) en su cuota decimal equivalente (1 / probabilidad). */
    double aCuota(double probabilidad) {
        if (probabilidad <= 0.0) {
            return Double.POSITIVE_INFINITY;
        }
        return 1.0 / probabilidad;
    }
}
