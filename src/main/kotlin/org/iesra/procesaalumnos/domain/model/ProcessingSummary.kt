package org.iesra.procesaalumnos.domain.model

/**
 * Resultado agregado del procesamiento completo.
 */
data class ProcessingSummary(
    val discoveredFiles: Int,
    val processedFiles: Int,
    val emailRecords: List<EmailRecord>,
    val assignments: List<GroupAssignment>,
    val issues: List<FileIssue>,
) {
    val groupMembersByGroup: Map<GroupCode, List<GroupAssignment>> =
        assignments.groupBy { it.assignedGroup }.toSortedMap(compareBy(GroupCode::value))
}
