package org.iesra.procesaalumnos

import kotlin.io.path.Path

/**
 * Punto de entrada de la versión simple del ejercicio.
 *
 * Lee los argumentos, ejecuta el procesamiento y muestra un resumen por consola.
 */
fun main(args: Array<String>) {
    val options = parseArgs(args)
    val summary = StudentProcessor().process(options.group, options.path)
    println("Ficheros detectados: ${summary.totalFiles}")
    println("Ficheros procesados correctamente: ${summary.processedFiles}")
    println("Ficheros con errores: ${summary.issues.size}")
    println("Correos creados correctamente: ${summary.emails.size}")
    println()
    println("Resumen de grupos:")
    summary.groups.toSortedMap().forEach { (group, students) ->
        println("- Grupo $group: ${students.size} alumnos")
    }
    if (summary.issues.isNotEmpty()) {
        println()
        println("Incidencias:")
        summary.issues.forEach { println("- archivo ${it.fileName}: ${it.message}") }
    }
}

/**
 * Convierte los argumentos de línea de comandos en una estructura tipada.
 *
 * @param args argumentos recibidos por `main`.
 * @return opciones mínimas necesarias para ejecutar el programa.
 * @throws IllegalStateException si falta `--grupo` o si una opción no tiene valor.
 */
private fun parseArgs(args: Array<String>): CliOptions {
    var group: String? = null
    var path = Path(".")
    var i = 0
    while (i < args.size) {
        when (args[i]) {
            "--grupo" -> {
                group = args.getOrNull(++i)?.takeIf(String::isNotBlank)
                    ?: error("Uso: --grupo <NOMBRE> [--path <RUTA>]")
            }

            "--path" -> {
                path = Path(args.getOrNull(++i)?.takeIf(String::isNotBlank)
                    ?: error("Uso: --grupo <NOMBRE> [--path <RUTA>]"))
            }

            else -> error("Opción no reconocida: ${args[i]}")
        }
        i++
    }
    return CliOptions(group ?: error("La opción --grupo es obligatoria"), path)
}

/**
 * Opciones de la ejecución desde línea de comandos.
 *
 * @property group identificador del curso, por ejemplo `DAW1`.
 * @property path directorio donde se buscarán los ficheros de entrada.
 */
data class CliOptions(val group: String, val path: java.nio.file.Path)
