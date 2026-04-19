package org.iesra.procesaalumnos.domain.model

/**
 * Fila lógica del fichero de correos.
 */
data class EmailRecord(
    val student: StudentRequest,
    val institutionalEmail: String,
)
