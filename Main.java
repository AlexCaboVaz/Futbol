package com.alex.probabilidades;

import com.alex.probabilidades.datos.CargadorDatos;
import com.alex.probabilidades.estadisticas.CalculadoraEstadisticas;
import com.alex.probabilidades.estadisticas.EstadisticasEquipo;
import com.alex.probabilidades.modelo.Partido;
import com.alex.probabilidades.probabilidad.CalculadoraProbabilidades;
import com.alex.probabilidades.probabilidad.ResultadoProbabilidad;

import java.nio.file.Path;
import java.util.List;

/**
 * Punto de entrada de demostración. Este motor está pensado para usarse como
 * librería (o desde sus tests), pero este main permite probarlo rápido desde
 * la terminal:
 *
 *   java -jar target/motor-probabilidades-deportivas.jar data/ejemplo_resultados.csv "Real Madrid" "Barcelona"
 *
 * Si no se pasan argumentos, usa el archivo de ejemplo incluido en /data.
 */
public class Main {

    public static void main(String[] args) {
        String rutaArchivo = args.length > 0 ? args[0] : "data/ejemplo_resultados.csv";
        String equipoLocal = args.length > 1 ? args[1] : "Real Madrid";
        String equipoVisitante = args.length > 2 ? args[2] : "Barcelona";

        CargadorDatos cargador = new CargadorDatos();
        List<Partido> partidos = cargador.cargar(Path.of(rutaArchivo));

        System.out.println("📊 " + partidos.size() + " partidos cargados desde " + rutaArchivo + "\n");

        CalculadoraEstadisticas calculadoraEstadisticas = new CalculadoraEstadisticas();
        mostrarEstadisticas(calculadoraEstadisticas.calcular(partidos, equipoLocal));
        mostrarEstadisticas(calculadoraEstadisticas.calcular(partidos, equipoVisitante));

        CalculadoraProbabilidades calculadoraProbabilidades = new CalculadoraProbabilidades(calculadoraEstadisticas);
        ResultadoProbabilidad resultado = calculadoraProbabilidades.calcular(partidos, equipoLocal, equipoVisitante);

        System.out.println("\n🔮 Simulación del próximo encuentro:");
        System.out.println(resultado);
    }

    private static void mostrarEstadisticas(EstadisticasEquipo e) {
        System.out.printf(
                "%-15s | PJ:%2d  V:%2d  E:%2d  D:%2d  | GF:%3d GC:%3d (%.2f - %.2f)  | Racha: %-3s  | Últimos: %s%n",
                e.equipo(), e.partidosJugados(), e.victorias(), e.empates(), e.derrotas(),
                e.golesFavor(), e.golesContra(), e.promedioGolesFavor(), e.promedioGolesContra(),
                e.racha(), e.ultimosResultados()
        );
    }
}
