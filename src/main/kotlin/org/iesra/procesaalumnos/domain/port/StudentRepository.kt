package org.iesra.procesaalumnos.domain.port

import java.nio.file.Path

interface StudentRepository {
    fun listStudentFiles(directory: Path): List<Path>
    fun readContent(file: Path): String
    fun moveToProcessed(file: Path, processedDirectory: Path)
}
