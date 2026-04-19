package org.iesra.procesaalumnos.application

import org.iesra.procesaalumnos.cli.CommandLineOptions
import org.iesra.procesaalumnos.domain.model.AssignmentReason
import org.iesra.procesaalumnos.domain.model.EmailRecord
import org.iesra.procesaalumnos.domain.model.FileIssue
import org.iesra.procesaalumnos.domain.model.GroupAssignment
import org.iesra.procesaalumnos.domain.model.ParseStudentResult
import org.iesra.procesaalumnos.domain.model.ProcessingSummary
import org.iesra.procesaalumnos.domain.model.StudentRequest
import org.iesra.procesaalumnos.domain.port.GroupAllocator
import org.iesra.procesaalumnos.domain.port.InstitutionalEmailGenerator
import org.iesra.procesaalumnos.domain.port.ProcessingWriter
import org.iesra.procesaalumnos.domain.port.StudentFileParser
import org.iesra.procesaalumnos.domain.port.StudentRepository
import kotlin.io.path.name

/**
 * Caso de uso principal: lee ficheros, procesa alumnado y genera las salidas.
 */
class StudentProcessingService(
    private val repository: StudentRepository,
    private val studentFileParser: StudentFileParser,
    private val emailGenerator: InstitutionalEmailGenerator,
    private val groupAllocator: GroupAllocator,
    private val writer: ProcessingWriter,
) {

    /**
     * Ejecuta el flujo completo del ejercicio.
     *
     * @param options parámetros de línea de comandos ya validados.
     * @return resumen agregado de toda la ejecución.
     */
    fun process(options: CommandLineOptions): ProcessingSummary {
        val files = repository.listStudentFiles(options.inputDirectory)
        val processedDirectory = options.inputDirectory.resolve(PROCESSED_DIRECTORY)
        val validStudents = mutableListOf<StudentRequest>()
        val issues = mutableListOf<FileIssue>()

        files.forEach { file ->
            val parseResult = runCatching {
                val content = repository.readContent(file)
                studentFileParser.parse(file, content)
            }.getOrElse { error ->
                ParseStudentResult.Failure(FileIssue(file.name, error.message ?: "No se pudo leer el fichero."))
            }

            when (parseResult) {
                is ParseStudentResult.Success -> validStudents += parseResult.student
                is ParseStudentResult.Failure -> issues += parseResult.issue
            }

            repository.moveToProcessed(file, processedDirectory)
        }

        val emails = validStudents.mapNotNull { student ->
            runCatching { EmailRecord(student, emailGenerator.generate(student)) }
                .getOrElse { error ->
                    issues += FileIssue(student.fileName, error.message ?: "No se pudo generar el correo institucional.")
                    null
                }
        }

        val allocationResult = groupAllocator.assign(emails.map(EmailRecord::student))
        issues += allocationResult.issues
        issues += buildReassignmentIssues(allocationResult.assignments)

        writer.writeEmails(options.inputDirectory, options.courseGroup, emails)
        writer.writeGroups(options.inputDirectory, options.courseGroup, allocationResult.assignments)

        return ProcessingSummary(
            discoveredFiles = files.size,
            processedFiles = validStudents.size,
            emailRecords = emails,
            assignments = allocationResult.assignments,
            issues = issues.sortedBy(FileIssue::fileName),
        )
    }

    private fun buildReassignmentIssues(assignments: List<GroupAssignment>): List<FileIssue> =
        assignments.mapNotNull { assignment ->
            when (assignment.reason) {
                AssignmentReason.REQUESTED -> null
                AssignmentReason.REASSIGNED_GROUP_MISSING ->
                    FileIssue(assignment.student.fileName, "grupo no informado, asignado aleatoriamente a ${assignment.assignedGroup.value}")

                AssignmentReason.REASSIGNED_GROUP_INVALID ->
                    FileIssue(assignment.student.fileName, "grupo no válido, asignado aleatoriamente a ${assignment.assignedGroup.value}")

                AssignmentReason.REASSIGNED_GROUP_FULL ->
                    FileIssue(assignment.student.fileName, "grupo solicitado lleno, asignado aleatoriamente a ${assignment.assignedGroup.value}")
            }
        }

    private companion object {
        const val PROCESSED_DIRECTORY = "procesados"
    }
}
