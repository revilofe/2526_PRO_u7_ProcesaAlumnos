package org.iesra.procesaalumnos.cli

import java.nio.file.Path
import kotlin.io.path.Path

/**
 * Se encarga de una única responsabilidad:
 * convertir los argumentos de línea de comandos en un objeto `CliOptions`.
 *
 * Esta clase es un buen primer ejemplo de orientación a objetos:
 * agrupamos en un mismo lugar el comportamiento relacionado con el parseo.
 */
class CommandLineParser {

    /**
     * Analiza las opciones recibidas desde `main`.
     *
     * Opciones soportadas:
     * - `--grupo <NOMBRE>` obligatorio
     * - `--path <RUTA>` opcional
     *
     * @param args argumentos recibidos al ejecutar el programa.
     * @return objeto con los parámetros ya interpretados.
     * @throws IllegalStateException si falta `--grupo`, si una opción no tiene valor
     * o si se usa una opción no reconocida.
     */
    fun parse(args: Array<String>): CliOptions {
        var group: String? = null
        var path: Path = Path(".")
        var index = 0

        while (index < args.size) {
            when (args[index]) {
                OPTION_GROUP -> {
                    group = args.getOrNull(index + 1)?.takeIf(String::isNotBlank)
                        ?: error("La opción $OPTION_GROUP necesita un valor. Uso: --grupo <NOMBRE> [--path <RUTA>]")
                    index += 2
                }

                OPTION_PATH -> {
                    val rawPath = args.getOrNull(index + 1)?.takeIf(String::isNotBlank)
                        ?: error("La opción $OPTION_PATH necesita un valor. Uso: --grupo <NOMBRE> [--path <RUTA>]")
                    path = Path(rawPath)
                    index += 2
                }

                else -> error("Opción no reconocida: ${args[index]}. Uso: --grupo <NOMBRE> [--path <RUTA>]")
            }
        }

        return CliOptions(
            group = group ?: error("La opción --grupo es obligatoria."),
            path = path,
        )
    }

    companion object {
        private const val OPTION_GROUP = "--grupo"
        private const val OPTION_PATH = "--path"
    }
}
