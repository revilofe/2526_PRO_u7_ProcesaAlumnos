package org.iesra.procesaalumnos.domain.model

import java.nio.file.Path

/**
 * Representa un fichero de entrada del alumnado.
 *
 * Es útil para recordar que en orientación a objetos también podemos modelar
 * conceptos del sistema que no son solo "personas" o "grupos".
 */
data class StudentFile(
    val path: Path,
)
