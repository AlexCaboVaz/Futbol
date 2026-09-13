# ⚽ Motor de Cálculo de Probabilidades Deportivas

Motor de cálculo **sin interfaz gráfica**, escrito en **Java 17 + Maven**, enfocado 100% en la lógica matemática. Acepta datos estructurados (CSV o JSON) con resultados históricos de fútbol y aplica un algoritmo propio (basado en la **distribución de Poisson**, el mismo enfoque que usan muchos modelos reales de analítica deportiva) para calcular tendencias, rachas, y simular probabilidades/cuotas para futuros encuentros.

El proyecto está desarrollado con **TDD (Desarrollo Guiado por Pruebas)**: cada clase de lógica tiene una batería de tests JUnit 5 que cubre tanto los casos normales como los límite (equipos sin historial, datos corruptos, divisiones por cero, etc.).

## ✨ Funcionalidades

- **Carga de datos** desde CSV o JSON con resultados históricos (`CargadorDatos`).
- **Estadísticas por equipo**: victorias, empates, derrotas, goles a favor/en contra, promedio de goles como local y como visitante (`CalculadoraEstadisticas`).
- **Racha actual** del equipo (ej. `"3V"` = 3 victorias seguidas) y los últimos resultados (ej. `"VDEVV"`).
- **Simulación de probabilidades** de un futuro encuentro mediante un modelo de Poisson, con conversión a cuota decimal (`CalculadoraProbabilidades`).

## 📦 Requisitos

- Java 17 o superior
- Maven 3.8+

## 🚀 Cómo compilar y ejecutar los tests (TDD)

```bash
mvn test
```

Esto ejecuta las tres baterías de tests (`CargadorDatosTest`, `CalculadoraEstadisticasTest`, `CalculadoraProbabilidadesTest`).

## ▶️ Ejecutar la demo por consola

```bash
mvn package
java -jar target/motor-probabilidades-deportivas.jar data/ejemplo_resultados.csv "Real Madrid" "Barcelona"
```

Salida esperada (con los datos de ejemplo incluidos):
```
📊 18 partidos cargados desde data/ejemplo_resultados.csv

Real Madrid     | PJ: 9  V: 8  E: 1  D: 0  | GF: 21 GC:  3 (2.33 - 0.33)  | Racha: 1E   | Últimos: VVVVE
Barcelona       | PJ: 9  V: 5  E: 2  D: 2  | GF: 17 GC:  6 (1.89 - 0.67)  | Racha: 1E   | Últimos: DVVVE

🔮 Simulación del próximo encuentro:
Real Madrid vs Barcelona
  Goles esperados: 1.57 - 0.53
  P(Local)=63.2% (cuota 1.58)  P(Empate)=24.7% (cuota 4.05)  P(Visitante)=12.1% (cuota 8.27)
```

## 📁 Formato de los datos de entrada

**CSV** (con cabecera):
```
fecha,equipo_local,equipo_visitante,goles_local,goles_visitante
2024-03-10,Real Madrid,Barcelona,2,1
```

**JSON** (array de objetos con las mismas claves, en camelCase):
```json
[
  {"fecha":"2024-03-10","equipoLocal":"Real Madrid","equipoVisitante":"Barcelona","golesLocal":2,"golesVisitante":1}
]
```

## 🧠 Cómo funciona el algoritmo de probabilidades

1. Se calcula el promedio de goles marcados/encajados de cada equipo como local y como visitante, y el promedio de goles de toda la liga cargada.
2. Se estiman los "goles esperados" de cada equipo para el encuentro (modelo de fuerza relativa de ataque/defensa, normalizado por el promedio de la liga).
3. Se simula la probabilidad de cada marcador posible (0-0, 1-0, 2-1... hasta 10-10) usando la distribución de Poisson.
4. Se suman las probabilidades según si el marcador implica victoria local, empate o victoria visitante.
5. Cada probabilidad se convierte en cuota decimal (`cuota = 1 / probabilidad`).

## 🧱 Estructura del proyecto

```
motor-probabilidades-deportivas/
├── pom.xml
├── data/
│   └── ejemplo_resultados.csv
├── src/main/java/com/alex/probabilidades/
│   ├── Main.java
│   ├── modelo/Partido.java
│   ├── datos/CargadorDatos.java
│   ├── datos/DatosInvalidosException.java
│   ├── estadisticas/CalculadoraEstadisticas.java
│   ├── estadisticas/EstadisticasEquipo.java
│   └── probabilidad/CalculadoraProbabilidades.java
│   └── probabilidad/ResultadoProbabilidad.java
└── src/test/java/com/alex/probabilidades/
    ├── datos/CargadorDatosTest.java
    ├── estadisticas/CalculadoraEstadisticasTest.java
    └── probabilidad/CalculadoraProbabilidadesTest.java
```

## 💡 Por qué existe esta herramienta

Es un escenario pensado para lucirse con TDD: la lógica matemática (rachas, promedios, distribución de Poisson) es fácil de testear de forma exhaustiva, incluyendo casos límite reales (equipo sin partidos como local, archivo de datos corrupto, probabilidad cero, etc.), sin necesidad de interfaz gráfica ni base de datos.
