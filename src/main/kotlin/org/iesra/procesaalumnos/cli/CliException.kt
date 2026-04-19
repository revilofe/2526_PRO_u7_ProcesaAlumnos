package org.iesra.procesaalumnos.cli

/**
 * Señala errores de validación en la línea de comandos.
 */
class CliException(message: String) : IllegalArgumentException(message)
