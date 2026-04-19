package org.iesra.procesaalumnos

import kotlin.io.path.Path

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

data class CliOptions(val group: String, val path: java.nio.file.Path)
