package org.iesra.procesaalumnos.domain.model

/**
 * Resultado de parsear un fichero de entrada.
 */
sealed interface ParseStudentResult {
    data class Success(val student: StudentRequest) : ParseStudentResult
    data class Failure(val issue: FileIssue) : ParseStudentResult
}
