package org.iesra.procesaalumnos.domain.model

import java.nio.file.Path

/**
 * Datos de alumnado ya validados mínimamente tras leer un fichero de entrada.
 */
data class StudentRequest(
    val sourceFile: Path,
    val fileName: String,
    val name: String,
    val surnames: String,
    val externalEmail: String,
    val requestedGroupRaw: String?,
) {
    val fullName: String = "$name $surnames"
}
