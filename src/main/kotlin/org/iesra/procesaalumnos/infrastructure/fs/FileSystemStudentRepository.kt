package org.iesra.procesaalumnos.infrastructure.fs

import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.moveTo
import kotlin.io.path.name
import kotlin.io.path.readText
import org.iesra.procesaalumnos.domain.port.StudentRepository

/**
 * Adaptador de sistema de ficheros local para leer y mover los ficheros del alumnado.
 */
class FileSystemStudentRepository : StudentRepository {
    override fun listStudentFiles(directory: Path): List<Path> {
        require(directory.exists() && directory.isDirectory()) {
            "El directorio de entrada no existe o no es válido: $directory"
        }

        return directory.toFile()
            .listFiles { file -> file.isFile && file.extension.equals("txt", ignoreCase = true) }
            ?.map { it.toPath() }
            ?.sortedBy { it.name }
            .orEmpty()
    }

    override fun readContent(file: Path): String = file.readText()

    override fun moveToProcessed(file: Path, processedDirectory: Path) {
        processedDirectory.createDirectories()
        file.moveTo(processedDirectory.resolve(file.name), overwrite = true)
    }
}
