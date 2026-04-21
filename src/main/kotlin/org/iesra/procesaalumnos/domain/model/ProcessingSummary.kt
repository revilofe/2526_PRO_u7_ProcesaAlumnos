package org.iesra.procesaalumnos.domain.model

/**
 * Resume el resultado final de la ejecución.
 *
 * Esta clase todavía es mínima, pero sirve para enseñar que el resumen también puede modelarse como un objeto.
 */
data class ProcessingSummary(
    val detectedFiles: Int,
    val validStudents: Int,
    val issues: List<FileIssue>,
)
