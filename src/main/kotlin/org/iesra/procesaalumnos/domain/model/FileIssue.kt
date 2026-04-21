package org.iesra.procesaalumnos.domain.model

/**
 * Representa un problema detectado durante el procesamiento.
 *
 * Es un ejemplo de objeto de dominio sencillo: da nombre y estructura
 * a una idea del problema en lugar de usar variables sueltas.
 */
data class FileIssue(
    val fileName: String,
    val message: String,
)
