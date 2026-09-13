package com.alex.probabilidades.estadisticas;

import com.alex.probabilidades.modelo.Partido;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraEstadisticasTest {

    private final CalculadoraEstadisticas calculadora = new CalculadoraEstadisticas();

    /**
     * Histórico de ejemplo para "Equipo A", en orden cronológico:
     *  1) A 3-1 B  -> victoria de A
     *  2) C 0-0 A  -> empate de A
     *  3) A 2-0 D  -> victoria de A
     *  4) A 1-2 B  -> derrota de A
     *  5) D 1-1 A  -> empate de A
     *  6) A 4-0 C  -> victoria de A
     *  7) A 2-1 D  -> victoria de A   (racha actual: 2 victorias seguidas)
     */
    private List<Partido> historicoDeEjemplo() {
        return List.of(
                new Partido(LocalDate.of(2024, 1, 1), "A", "B", 3, 1),
                new Partido(LocalDate.of(2024, 1, 8), "C", "A", 0, 0),
                new Partido(LocalDate.of(2024, 1, 15), "A", "D", 2, 0),
                new Partido(LocalDate.of(2024, 1, 22), "A", "B", 1, 2),
                new Partido(LocalDate.of(2024, 1, 29), "D", "A", 1, 1),
                new Partido(LocalDate.of(2024, 2, 5), "A", "C", 4, 0),
                new Partido(LocalDate.of(2024, 2, 12), "A", "D", 2, 1)
        );
    }

    @Test
    void calculaVictoriasEmpatesYDerrotasCorrectamente() {
        EstadisticasEquipo stats = calculadora.calcular(historicoDeEjemplo(), "A");

        assertEquals(7, stats.partidosJugados());
        assertEquals(4, stats.victorias());
        assertEquals(2, stats.empates());
        assertEquals(1, stats.derrotas());
    }

    @Test
    void calculaGolesAFavorYEnContraCorrectamente() {
        EstadisticasEquipo stats = calculadora.calcular(historicoDeEjemplo(), "A");

        // Goles a favor: 3 + 0 + 2 + 1 + 1 + 4 + 2 = 13
        assertEquals(13, stats.golesFavor());
        // Goles en contra: 1 + 0 + 0 + 2 + 1 + 0 + 1 = 5
        assertEquals(5, stats.golesContra());
        assertEquals(13.0 / 7, stats.promedioGolesFavor(), 0.0001);
        assertEquals(5.0 / 7, stats.promedioGolesContra(), 0.0001);
    }

    @Test
    void calculaLaRachaActualDeVictorias() {
        EstadisticasEquipo stats = calculadora.calcular(historicoDeEjemplo(), "A");

        assertEquals("2V", stats.racha());
    }

    @Test
    void calculaLosUltimosResultadosEnOrdenCronologico() {
        EstadisticasEquipo stats = calculadora.calcular(historicoDeEjemplo(), "A");

        // Los últimos 5 partidos de A, en orden (partidos 3 a 7): V, D, E, V, V
        assertEquals("VDEVV", stats.ultimosResultados());
    }

    @Test
    void esInsensibleAMayusculasYMinusculasEnElNombreDelEquipo() {
        EstadisticasEquipo stats = calculadora.calcular(historicoDeEjemplo(), "a");

        assertEquals(7, stats.partidosJugados());
    }

    @Test
    void lanzaExcepcionSiElEquipoNoTienePartidos() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calculadora.calcular(historicoDeEjemplo(), "Equipo Inexistente")
        );
    }

    @Test
    void calculaElPromedioDeGolesComoLocalYVisitante() {
        List<Partido> partidos = historicoDeEjemplo();

        // A como local: partidos 1,3,4,6,7 -> goles marcados: 3,2,1,4,2 = 12 / 5 = 2.4
        assertEquals(2.4, calculadora.promedioGolesMarcadosComoLocal(partidos, "A"), 0.0001);
        // A como local, goles encajados: 1,0,2,0,1 = 4 / 5 = 0.8
        assertEquals(0.8, calculadora.promedioGolesEncajadosComoLocal(partidos, "A"), 0.0001);

        // A como visitante: partidos 2,5 -> goles marcados: 0,1 = 1 / 2 = 0.5
        assertEquals(0.5, calculadora.promedioGolesMarcadosComoVisitante(partidos, "A"), 0.0001);
        // A como visitante, goles encajados: 0,1 = 1 / 2 = 0.5
        assertEquals(0.5, calculadora.promedioGolesEncajadosComoVisitante(partidos, "A"), 0.0001);
    }

    @Test
    void lanzaExcepcionSiElEquipoNuncaJugoComoLocal() {
        List<Partido> partidos = List.of(
                new Partido(LocalDate.of(2024, 1, 1), "B", "A", 1, 1)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> calculadora.promedioGolesMarcadosComoLocal(partidos, "A")
        );
    }

    @Test
    void calculaElPromedioDeGolesPorPartidoDeLaLiga() {
        List<Partido> partidos = List.of(
                new Partido(LocalDate.of(2024, 1, 1), "A", "B", 2, 1), // 3 goles
                new Partido(LocalDate.of(2024, 1, 8), "C", "D", 0, 0)  // 0 goles
        );

        // Total goles = 3, partidos = 2 -> promedio por partido "de un equipo" = 3 / (2*2) = 0.75
        assertEquals(0.75, calculadora.promedioGolesPorPartidoLiga(partidos), 0.0001);
    }

    @Test
    void lanzaExcepcionSiNoHayPartidosParaElPromedioDeLaLiga() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calculadora.promedioGolesPorPartidoLiga(List.of())
        );
    }
}
