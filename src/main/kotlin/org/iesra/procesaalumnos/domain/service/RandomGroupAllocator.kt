package org.iesra.procesaalumnos.domain.service

import kotlin.random.Random
import org.iesra.procesaalumnos.domain.model.AssignmentReason
import org.iesra.procesaalumnos.domain.model.FileIssue
import org.iesra.procesaalumnos.domain.model.GroupAssignment
import org.iesra.procesaalumnos.domain.model.GroupCode
import org.iesra.procesaalumnos.domain.model.StudentRequest
import org.iesra.procesaalumnos.domain.port.AllocationResult
import org.iesra.procesaalumnos.domain.port.GroupAllocator

/**
 * Reparte alumnado entre grupos con capacidad máxima y crea grupos nuevos cuando es necesario.
 */
class RandomGroupAllocator(
    private val availableGroups: List<GroupCode> = listOf("A", "B", "C", "D").map(GroupCode::of),
    private val groupCapacity: Int = 5,
    private val random: Random,
) : GroupAllocator {
    init {
        require(availableGroups.isNotEmpty()) { "Debe existir al menos un grupo disponible." }
        require(groupCapacity > 0) { "La capacidad máxima debe ser positiva." }
    }

    override fun assign(students: List<StudentRequest>): AllocationResult {
        val assignments = mutableListOf<GroupAssignment>()
        val issues = mutableListOf<FileIssue>()
        val activeGroups = availableGroups.toMutableList()
        val occupancy = activeGroups.associateWith { 0 }.toMutableMap()

        students.forEach { student ->
            val requestedGroup = GroupCode.fromOrNull(student.requestedGroupRaw)
            val directReason = when {
                student.requestedGroupRaw.isNullOrBlank() -> AssignmentReason.REASSIGNED_GROUP_MISSING
                requestedGroup == null || requestedGroup !in occupancy -> AssignmentReason.REASSIGNED_GROUP_INVALID
                else -> AssignmentReason.REQUESTED
            }

            val assignedGroup = when {
                directReason == AssignmentReason.REQUESTED && hasFreeSpace(requestedGroup!!, occupancy) -> requestedGroup
                else -> pickAvailableOrCreateNextGroup(activeGroups, occupancy)
            }

            val finalReason = when {
                directReason == AssignmentReason.REQUESTED && assignedGroup == requestedGroup -> AssignmentReason.REQUESTED
                directReason == AssignmentReason.REQUESTED -> AssignmentReason.REASSIGNED_GROUP_FULL
                else -> directReason
            }

            occupancy[assignedGroup] = occupancy.getValue(assignedGroup) + 1
            assignments += GroupAssignment(student, assignedGroup, finalReason)
        }

        return AllocationResult(assignments, issues)
    }

    private fun hasFreeSpace(group: GroupCode, occupancy: Map<GroupCode, Int>): Boolean =
        occupancy.getValue(group) < groupCapacity

    private fun pickAvailableOrCreateNextGroup(
        activeGroups: MutableList<GroupCode>,
        occupancy: MutableMap<GroupCode, Int>,
    ): GroupCode {
        val candidates = activeGroups.filter { hasFreeSpace(it, occupancy) }
        if (candidates.isNotEmpty()) {
            return candidates[random.nextInt(candidates.size)]
        }

        val nextGroup = nextGroupAfter(activeGroups.maxBy(GroupCode::value))
        activeGroups += nextGroup
        occupancy[nextGroup] = 0
        return nextGroup
    }

    private fun nextGroupAfter(lastGroup: GroupCode): GroupCode {
        val lastLetter = lastGroup.value.single()
        require(lastLetter < 'Z') { "No es posible crear más grupos después de ${lastGroup.value}." }
        return GroupCode.of((lastLetter + 1).toString())
    }
}
