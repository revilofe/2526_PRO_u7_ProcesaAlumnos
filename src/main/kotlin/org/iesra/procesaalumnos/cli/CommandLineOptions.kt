package org.iesra.procesaalumnos.cli

import java.nio.file.Path

/**
 * Parámetros efectivos de la ejecución del caso de uso.
 */
data class CommandLineOptions(
    val courseGroup: String,
    val inputDirectory: Path,
)
