package org.iesra.procesaalumnos.application

import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.iesra.procesaalumnos.cli.CommandLineOptions
import org.iesra.procesaalumnos.domain.model.AssignmentReason
import org.iesra.procesaalumnos.domain.model.EmailRecord
import org.iesra.procesaalumnos.domain.model.FileIssue
import org.iesra.procesaalumnos.domain.model.GroupAssignment
import org.iesra.procesaalumnos.domain.model.GroupCode
import org.iesra.procesaalumnos.domain.model.ParseStudentResult
import org.iesra.procesaalumnos.domain.model.StudentRequest
import org.iesra.procesaalumnos.domain.port.AllocationResult
import org.iesra.procesaalumnos.domain.port.GroupAllocator
import org.iesra.procesaalumnos.domain.port.InstitutionalEmailGenerator
import org.iesra.procesaalumnos.domain.port.ProcessingWriter
import org.iesra.procesaalumnos.domain.port.StudentFileParser
import org.iesra.procesaalumnos.domain.port.StudentRepository

class StudentProcessingServiceTest {
    @Test
    fun `procesa alumnos validos genera salidas y mueve todos los ficheros`() {
        val repository = FakeStudentRepository(
            mapOf(
                Path("uno.txt") to """
                    Nombre: Ana
                    Apellidos: Lopez Perez
                    email: uno@g.educaand.es
                    Grupo = A
                """.trimIndent(),
                Path("dos.txt") to "contenido invalido",
            ),
        )
        val writer = FakeProcessingWriter()

        val service = StudentProcessingService(
            repository = repository,
            studentFileParser = StudentFileParser { file, content ->
                if ("Nombre:" !in content) {
                    ParseStudentResult.Failure(FileIssue(file.fileName.toString(), "Formato inválido"))
                } else {
                    ParseStudentResult.Success(
                        StudentRequest(file, file.fileName.toString(), "Ana", "Lopez Perez", "uno@g.educaand.es", "A"),
                    )
                }
            },
            emailGenerator = InstitutionalEmailGenerator { "aperezr@iesrafaelalberti.es" },
            groupAllocator = GroupAllocator {
                AllocationResult(
                    assignments = listOf(
                        GroupAssignment(
                            StudentRequest(Path("uno.txt"), "uno.txt", "Ana", "Lopez Perez", "uno@g.educaand.es", "A"),
                            GroupCode.of("A"),
                            AssignmentReason.REQUESTED,
                        ),
                    ),
                    issues = emptyList(),
                )
            },
            writer = writer,
        )

        val summary = service.process(CommandLineOptions("DAW1", Path(".")))

        assertEquals(2, summary.discoveredFiles)
        assertEquals(1, summary.processedFiles)
        assertEquals(1, summary.emailRecords.size)
        assertEquals(1, summary.assignments.size)
        assertEquals(1, summary.issues.size)
        assertEquals(2, repository.movedFiles.size)
        assertEquals("DAW1", writer.courseGroup)
        assertEquals(1, writer.emails.size)
        assertEquals(1, writer.assignments.size)
        assertTrue(writer.outputDirectory.toString().isNotBlank())
    }

    private class FakeStudentRepository(private val files: Map<Path, String>) : StudentRepository {
        val movedFiles = mutableListOf<Path>()
        override fun listStudentFiles(directory: Path): List<Path> = files.keys.sortedBy(Path::toString)
        override fun readContent(file: Path): String = files.getValue(file)
        override fun moveToProcessed(file: Path, processedDirectory: Path) {
            movedFiles.add(file)
        }
    }

    private class FakeProcessingWriter : ProcessingWriter {
        lateinit var outputDirectory: Path
        lateinit var courseGroup: String
        var emails: List<EmailRecord> = emptyList()
        var assignments: List<GroupAssignment> = emptyList()

        override fun writeEmails(outputDirectory: Path, courseGroup: String, emails: List<EmailRecord>) {
            this.outputDirectory = outputDirectory
            this.courseGroup = courseGroup
            this.emails = emails
        }

        override fun writeGroups(outputDirectory: Path, courseGroup: String, assignments: List<GroupAssignment>) {
            this.outputDirectory = outputDirectory
            this.courseGroup = courseGroup
            this.assignments = assignments
        }
    }
}
