package org.iesra.procesaalumnos.domain.port

import org.iesra.procesaalumnos.domain.model.FileIssue
import org.iesra.procesaalumnos.domain.model.GroupAssignment
import org.iesra.procesaalumnos.domain.model.StudentRequest

fun interface GroupAllocator {
    fun assign(students: List<StudentRequest>): AllocationResult
}

data class AllocationResult(
    val assignments: List<GroupAssignment>,
    val issues: List<FileIssue>,
)
