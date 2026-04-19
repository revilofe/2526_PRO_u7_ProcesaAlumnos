package org.iesra.procesaalumnos.domain.model

/**
 * Incidencia registrada durante la lectura o el procesamiento de un fichero.
 */
data class FileIssue(
    val fileName: String,
    val message: String,
)
