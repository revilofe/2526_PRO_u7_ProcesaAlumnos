package org.iesra.procesaalumnos.domain.port

import java.nio.file.Path
import org.iesra.procesaalumnos.domain.model.EmailRecord
import org.iesra.procesaalumnos.domain.model.GroupAssignment

interface ProcessingWriter {
    fun writeEmails(outputDirectory: Path, courseGroup: String, emails: List<EmailRecord>)
    fun writeGroups(outputDirectory: Path, courseGroup: String, assignments: List<GroupAssignment>)
}
