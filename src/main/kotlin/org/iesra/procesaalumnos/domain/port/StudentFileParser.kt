package org.iesra.procesaalumnos.domain.port

import java.nio.file.Path
import org.iesra.procesaalumnos.domain.model.ParseStudentResult

fun interface StudentFileParser {
    fun parse(file: Path, content: String): ParseStudentResult
}
