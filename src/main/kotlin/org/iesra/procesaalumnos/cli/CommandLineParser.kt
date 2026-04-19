package org.iesra.procesaalumnos.cli

import java.nio.file.Path
import kotlin.io.path.Path

/**
 * Convierte los argumentos de línea de comandos en opciones tipadas de ejecución.
 */
class CommandLineParser {

    /**
     * Analiza los argumentos recibidos por `main`.
     *
     * @throws CliException si falta `--grupo` o la sintaxis no es válida.
     */
    fun parse(args: Array<String>): CommandLineOptions {
        if (args.isEmpty()) {
            throw CliException(usage())
        }

        var courseGroup: String? = null
        var inputDirectory: Path = Path(".")
        var index = 0

        while (index < args.size) {
            when (val option = args[index]) {
                OPTION_GROUP -> {
                    courseGroup = args.getOrNull(index + 1)?.takeIf { it.isNotBlank() }
                        ?: throw CliException("La opción $OPTION_GROUP requiere un valor.\n${usage()}")
                    index += 2
                }

                OPTION_PATH -> {
                    val rawPath = args.getOrNull(index + 1)?.takeIf { it.isNotBlank() }
                        ?: throw CliException("La opción $OPTION_PATH requiere un valor.\n${usage()}")
                    inputDirectory = Path(rawPath)
                    index += 2
                }

                else -> throw CliException("Opción no reconocida: $option\n${usage()}")
            }
        }

        return CommandLineOptions(
            courseGroup = courseGroup?.trim()?.takeIf(String::isNotEmpty)
                ?: throw CliException("La opción $OPTION_GROUP es obligatoria.\n${usage()}"),
            inputDirectory = inputDirectory.normalize(),
        )
    }

    private fun usage(): String = "Uso: procesa-alumnos --grupo <NOMBRE> [--path <RUTA>]"

    private companion object {
        const val OPTION_GROUP = "--grupo"
        const val OPTION_PATH = "--path"
    }
}
