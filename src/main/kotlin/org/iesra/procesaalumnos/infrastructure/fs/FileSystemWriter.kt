package org.iesra.procesaalumnos.infrastructure.fs

import java.nio.file.Path
import kotlin.io.path.writeText
import org.iesra.procesaalumnos.domain.model.EmailRecord
import org.iesra.procesaalumnos.domain.model.GroupAssignment
import org.iesra.procesaalumnos.domain.port.ProcessingWriter

/**
 * Escribe los ficheros de salida finales en el directorio de trabajo.
 */
class FileSystemWriter : ProcessingWriter {
    override fun writeEmails(outputDirectory: Path, courseGroup: String, emails: List<EmailRecord>) {
        val content = buildString {
            appendLine("nombre|apellidos|email1|email2")
            emails.forEach { record ->
                appendLine(
                    listOf(
                        record.student.name,
                        record.student.surnames,
                        record.student.externalEmail,
                        record.institutionalEmail,
                    ).joinToString("|"),
                )
            }
        }
        outputDirectory.resolve("$courseGroup-correos.csv").writeText(content)
    }

    override fun writeGroups(outputDirectory: Path, courseGroup: String, assignments: List<GroupAssignment>) {
        val grouped = assignments.groupBy { it.assignedGroup }.toSortedMap(compareBy { it.value })
        val content = buildString {
            grouped.forEach { (group, members) ->
                appendLine("Grupo ${group.value}")
                members.sortedBy { it.student.fullName }.forEach { appendLine("- ${it.student.fullName}") }
                appendLine()
            }
        }.trimEnd()

        outputDirectory.resolve("$courseGroup-grupos.txt").writeText(content)
    }
}
