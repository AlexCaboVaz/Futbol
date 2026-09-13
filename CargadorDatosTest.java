package com.alex.probabilidades.datos;

import com.alex.probabilidades.modelo.Partido;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CargadorDatosTest {

    private final CargadorDatos cargador = new CargadorDatos();

    @Test
    void cargaCorrectamenteUnCsvValido(@TempDir Path dirTemporal) throws IOException {
        Path archivo = dirTemporal.resolve("resultados.csv");
        Files.writeString(archivo, """
                fecha,equipo_local,equipo_visitante,goles_local,goles_visitante
                2024-01-14,Real Madrid,Sevilla,3,1
                2024-01-21,Barcelona,Valencia,2,0
                """);

        List<Partido> partidos = cargador.cargarDesdeCsv(archivo);

        assertEquals(2, partidos.size());
        Partido primero = partidos.get(0);
        assertEquals(LocalDate.of(2024, 1, 14), primero.fecha());
        assertEquals("Real Madrid", primero.equipoLocal());
        assertEquals("Sevilla", primero.equipoVisitante());
        assertEquals(3, primero.golesLocal());
        assertEquals(1, primero.golesVisitante());
    }

    @Test
    void ignoraLineasEnBlancoAlFinal(@TempDir Path dirTemporal) throws IOException {
        Path archivo = dirTemporal.resolve("resultados.csv");
        Files.writeString(archivo, """
                fecha,equipo_local,equipo_visitante,goles_local,goles_visitante
                2024-01-14,Real Madrid,Sevilla,3,1

                """);

        List<Partido> partidos = cargador.cargarDesdeCsv(archivo);

        assertEquals(1, partidos.size());
    }

    @Test
    void lanzaExcepcionSiElCsvSoloTieneCabecera(@TempDir Path dirTemporal) throws IOException {
        Path archivo = dirTemporal.resolve("vacio.csv");
        Files.writeString(archivo, "fecha,equipo_local,equipo_visitante,goles_local,goles_visitante\n");

        assertThrows(DatosInvalidosException.class, () -> cargador.cargarDesdeCsv(archivo));
    }

    @Test
    void lanzaExcepcionSiFaltanColumnas(@TempDir Path dirTemporal) throws IOException {
        Path archivo = dirTemporal.resolve("resultados.csv");
        Files.writeString(archivo, """
                fecha,equipo_local,equipo_visitante,goles_local,goles_visitante
                2024-01-14,Real Madrid,Sevilla,3
                """);

        DatosInvalidosException excepcion = assertThrows(
                DatosInvalidosException.class, () -> cargador.cargarDesdeCsv(archivo)
        );
        assertTrue(excepcion.getMessage().contains("Línea 2"));
    }

    @Test
    void lanzaExcepcionSiLaFechaEsInvalida(@TempDir Path dirTemporal) throws IOException {
        Path archivo = dirTemporal.resolve("resultados.csv");
        Files.writeString(archivo, """
                fecha,equipo_local,equipo_visitante,goles_local,goles_visitante
                31-13-2024,Real Madrid,Sevilla,3,1
                """);

        assertThrows(DatosInvalidosException.class, () -> cargador.cargarDesdeCsv(archivo));
    }

    @Test
    void lanzaExcepcionSiLosGolesNoSonNumericos(@TempDir Path dirTemporal) throws IOException {
        Path archivo = dirTemporal.resolve("resultados.csv");
        Files.writeString(archivo, """
                fecha,equipo_local,equipo_visitante,goles_local,goles_visitante
                2024-01-14,Real Madrid,Sevilla,tres,1
                """);

        assertThrows(DatosInvalidosException.class, () -> cargador.cargarDesdeCsv(archivo));
    }

    @Test
    void lanzaExcepcionSiLosGolesSonNegativos(@TempDir Path dirTemporal) throws IOException {
        Path archivo = dirTemporal.resolve("resultados.csv");
        Files.writeString(archivo, """
                fecha,equipo_local,equipo_visitante,goles_local,goles_visitante
                2024-01-14,Real Madrid,Sevilla,-1,1
                """);

        assertThrows(DatosInvalidosException.class, () -> cargador.cargarDesdeCsv(archivo));
    }

    @Test
    void cargaCorrectamenteUnJsonValido(@TempDir Path dirTemporal) throws IOException {
        Path archivo = dirTemporal.resolve("resultados.json");
        Files.writeString(archivo, """
                [
                  {"fecha":"2024-01-14","equipoLocal":"Real Madrid","equipoVisitante":"Sevilla","golesLocal":3,"golesVisitante":1}
                ]
                """);

        List<Partido> partidos = cargador.cargarDesdeJson(archivo);

        assertEquals(1, partidos.size());
        assertEquals("Real Madrid", partidos.get(0).equipoLocal());
    }

    @Test
    void lanzaExcepcionSiElJsonEstaVacio(@TempDir Path dirTemporal) throws IOException {
        Path archivo = dirTemporal.resolve("vacio.json");
        Files.writeString(archivo, "[]");

        assertThrows(DatosInvalidosException.class, () -> cargador.cargarDesdeJson(archivo));
    }

    @Test
    void elMetodoCargarDetectaElFormatoPorLaExtension(@TempDir Path dirTemporal) throws IOException {
        Path archivoCsv = dirTemporal.resolve("resultados.csv");
        Files.writeString(archivoCsv, """
                fecha,equipo_local,equipo_visitante,goles_local,goles_visitante
                2024-01-14,Real Madrid,Sevilla,3,1
                """);

        assertEquals(1, cargador.cargar(archivoCsv).size());
    }

    @Test
    void lanzaExcepcionParaExtensionNoSoportada(@TempDir Path dirTemporal) throws IOException {
        Path archivo = dirTemporal.resolve("resultados.txt");
        Files.writeString(archivo, "contenido irrelevante");

        assertThrows(DatosInvalidosException.class, () -> cargador.cargar(archivo));
    }
}
