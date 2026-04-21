package org.iesra.procesaalumnos

import org.iesra.procesaalumnos.application.StudentProcessingApplication
import org.iesra.procesaalumnos.cli.CommandLineParser

/**
 * Punto de entrada del proyecto base.
 *
 * La idea didáctica es separar responsabilidades:
 * - `main` solo coordina objetos.
 * - `CommandLineParser` interpreta los argumentos.
 * - `StudentProcessingApplication` representa el flujo principal del programa.
 */
fun main(args: Array<String>) {
    val parser = CommandLineParser()
    val options = parser.parse(args)

    val application = StudentProcessingApplication()
    application.run(options)
}
