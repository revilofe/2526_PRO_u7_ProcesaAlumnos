package org.iesra.procesaalumnos.domain.model

/**
 * Resultado de asignar un alumno a un subgrupo concreto.
 */
data class GroupAssignment(
    val student: StudentRequest,
    val assignedGroup: GroupCode,
    val reason: AssignmentReason,
)

enum class AssignmentReason {
    REQUESTED,
    REASSIGNED_GROUP_MISSING,
    REASSIGNED_GROUP_INVALID,
    REASSIGNED_GROUP_FULL,
}
