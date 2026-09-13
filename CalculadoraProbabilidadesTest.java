package com.alex.probabilidades.probabilidad;

import com.alex.probabilidades.modelo.Partido;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraProbabilidadesTest {

    private final CalculadoraProbabilidades calculadora = new CalculadoraProbabilidades();

    // ---------- Pruebas de la función de Poisson en sí misma ----------

    @Test
    void poissonDeMediaCeroEnCeroEsUno() {
        assertEquals(1.0, calculadora.poisson(0.0, 0), 0.0001);
    }

    @Test
    void poissonConMediaConocidaCoincideConElValorEsperado() {
        // P(X=0) para lambda=2 es e^-2 ≈ 0.13534
        assertEquals(0.13534, calculadora.poisson(2.0, 0), 0.0001);
        // P(X=2) para lambda=2 es (e^-2 * 2^2) / 2! ≈ 0.27067
        assertEquals(0.27067, calculadora.poisson(2.0, 2), 0.0001);
    }

    @Test
    void lasProbabilidadesDePoissonSumanAproximadamenteUno() {
        double suma = 0.0;
        for (int k = 0; k <= 30; k++) {
            suma += calculadora.poisson(3.0, k);
        }
        assertEquals(1.0, suma, 0.0001);
    }

    // ---------- Pruebas de conversión a cuota ----------

    @Test
    void convierteProbabilidadACuotaCorrectamente() {
        assertEquals(2.0, calculadora.aCuota(0.5), 0.0001);
        assertEquals(4.0, calculadora.aCuota(0.25), 0.0001);
    }

    @Test
    void unaProbabilidadDeCeroDaUnaCuotaInfinita() {
        assertEquals(Double.POSITIVE_INFINITY, calculadora.aCuota(0.0));
    }

    // ---------- Pruebas del cálculo completo ----------

    @Test
    void lasTresProbabilidadesSumanAproximadamenteUno() {
        List<Partido> partidos = historicoConEquipoDominante();

        ResultadoProbabilidad resultado = calculadora.calcular(partidos, "Fuerte", "Debil");

        double suma = resultado.probabilidadLocal() + resultado.probabilidadEmpate() + resultado.probabilidadVisitante();
        assertEquals(1.0, suma, 0.0001);
    }

    @Test
    void unEquipoClaramenteMasFuerteTieneMasProbabilidadDeGanar() {
        List<Partido> partidos = historicoConEquipoDominante();

        ResultadoProbabilidad resultado = calculadora.calcular(partidos, "Fuerte", "Debil");

        assertTrue(resultado.probabilidadLocal() > resultado.probabilidadVisitante());
        assertTrue(resultado.cuotaLocal() < resultado.cuotaVisitante());
    }

    @Test
    void dosEquiposIdenticosTienenLaMismaProbabilidadDeGanarComoLocalOVisitante() {
        List<Partido> partidos = historicoDeEquiposIdenticos();

        ResultadoProbabilidad resultado = calculadora.calcular(partidos, "X", "Y");

        assertEquals(resultado.probabilidadLocal(), resultado.probabilidadVisitante(), 0.0001);
    }

    @Test
    void lanzaExcepcionSiElEquipoLocalNoTieneHistoricoComoLocal() {
        List<Partido> partidos = List.of(
                new Partido(LocalDate.of(2024, 1, 1), "Otro", "Fuerte", 0, 1),
                new Partido(LocalDate.of(2024, 1, 8), "Debil", "Otro", 1, 1)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> calculadora.calcular(partidos, "Fuerte", "Debil")
        );
    }

    /** Histórico donde "Fuerte" marca y encaja mucho mejor que "Debil" en todos sus partidos. */
    private List<Partido> historicoConEquipoDominante() {
        return List.of(
                new Partido(LocalDate.of(2024, 1, 1), "Fuerte", "Otro", 3, 0),
                new Partido(LocalDate.of(2024, 1, 8), "Otro", "Fuerte", 0, 2),
                new Partido(LocalDate.of(2024, 1, 15), "Fuerte", "Debil", 4, 0),
                new Partido(LocalDate.of(2024, 1, 22), "Debil", "Otro", 0, 1),
                new Partido(LocalDate.of(2024, 1, 29), "Otro", "Debil", 2, 0),
                new Partido(LocalDate.of(2024, 2, 5), "Debil", "Fuerte", 0, 3)
        );
    }

    /** Histórico donde "X" e "Y" tienen exactamente el mismo rendimiento (todos sus partidos terminan 1-1). */
    private List<Partido> historicoDeEquiposIdenticos() {
        return List.of(
                new Partido(LocalDate.of(2024, 1, 1), "X", "Y", 1, 1),
                new Partido(LocalDate.of(2024, 1, 8), "Y", "X", 1, 1),
                new Partido(LocalDate.of(2024, 1, 15), "X", "Y", 1, 1),
                new Partido(LocalDate.of(2024, 1, 22), "Y", "X", 1, 1)
        );
    }
}
