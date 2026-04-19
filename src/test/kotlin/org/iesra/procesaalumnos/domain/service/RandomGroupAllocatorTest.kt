package org.iesra.procesaalumnos.domain.service

import kotlin.io.path.Path
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import org.iesra.procesaalumnos.domain.model.AssignmentReason
import org.iesra.procesaalumnos.domain.model.GroupCode
import org.iesra.procesaalumnos.domain.model.StudentRequest

class RandomGroupAllocatorTest {
    @Test
    fun `reasigna cuando el grupo solicitado esta lleno`() {
        val allocator = RandomGroupAllocator(
            availableGroups = listOf("A", "B").map(GroupCode::of),
            groupCapacity = 1,
            random = Random(0),
        )
        val students = listOf(
            createStudent("uno.txt", "Ana", "Lopez Perez", "uno@g.educaand.es", "A"),
            createStudent("dos.txt", "Luis", "Garcia Torres", "dos@g.educaand.es", "A"),
        )
        val result = allocator.assign(students)
        assertEquals(2, result.assignments.size)
        assertEquals(AssignmentReason.REQUESTED, result.assignments[0].reason)
        assertEquals(AssignmentReason.REASSIGNED_GROUP_FULL, result.assignments[1].reason)
        assertEquals("B", result.assignments[1].assignedGroup.value)
    }

    @Test
    fun `crea el siguiente grupo alfabetico cuando todos los existentes estan llenos`() {
        val allocator = RandomGroupAllocator(
            availableGroups = listOf("A", "B").map(GroupCode::of),
            groupCapacity = 1,
            random = Random(0),
        )
        val students = listOf(
            createStudent("uno.txt", "Ana", "Lopez Perez", "uno@g.educaand.es", "A"),
            createStudent("dos.txt", "Luis", "Garcia Torres", "dos@g.educaand.es", "B"),
            createStudent("tres.txt", "Marta", "Ruiz Gomez", "tres@g.educaand.es", "A"),
        )
        val result = allocator.assign(students)
        assertEquals(3, result.assignments.size)
        assertEquals("C", result.assignments[2].assignedGroup.value)
        assertEquals(AssignmentReason.REASSIGNED_GROUP_FULL, result.assignments[2].reason)
    }

    @Test
    fun `crea un grupo nuevo cuando no hay plazas en los existentes`() {
        val allocator = RandomGroupAllocator(
            availableGroups = listOf("A").map(GroupCode::of),
            groupCapacity = 1,
            random = Random(0),
        )
        val students = listOf(
            createStudent("uno.txt", "Ana", "Lopez Perez", "uno@g.educaand.es", "A"),
            createStudent("dos.txt", "Luis", "Garcia Torres", "dos@g.educaand.es", "B"),
        )
        val result = allocator.assign(students)
        assertEquals(2, result.assignments.size)
        assertEquals(0, result.issues.size)
        assertEquals("A", result.assignments[0].assignedGroup.value)
        assertEquals("B", result.assignments[1].assignedGroup.value)
        assertEquals(AssignmentReason.REASSIGNED_GROUP_INVALID, result.assignments[1].reason)
    }

    private fun createStudent(
        fileName: String,
        name: String,
        surnames: String,
        email: String,
        requestedGroup: String?,
    ) = StudentRequest(Path(fileName), fileName, name, surnames, email, requestedGroup)
}
