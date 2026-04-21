package org.iesra.procesaalumnos.application

import org.iesra.procesaalumnos.cli.CliOptions
import org.iesra.procesaalumnos.domain.model.FileIssue
import org.iesra.procesaalumnos.domain.model.ProcessingSummary
import org.iesra.procesaalumnos.domain.model.StudentFile
import java.nio.file.Path

/**
 * Coordina el caso de uso principal del programa.
 *
 * En una solución orientada a objetos, esta clase actúa como orquestador:
 * no hace necesariamente todo el trabajo por sí sola, sino que delega en
 * otros objetos especializados.
 *
 * Aquí solo dejamos implementado el arranque y el esqueleto comentado del flujo.
 */
class StudentProcessingApplication {

    /**
     * Ejecuta el flujo principal del programa.
     *
     * En esta base didáctica solo mostramos el parseo de parámetros ya resuelto
     * y dejamos el resto del proceso preparado como guía.
     *
     * @param options opciones recibidas desde la línea de comandos.
     */
    fun run(options: CliOptions) {
        println("Grupo recibido: ${options.group}")
        println("Directorio de trabajo: ${options.path}")

        // A partir de aquí, una solución OO razonable podría seguir este flujo:

        // 1. Pedir a un repositorio de ficheros que localice los `.txt` de entrada.
        // val inputFiles = studentFileRepository.findInputFiles(options.path)

        // 2. Crear una colección donde guardar alumnado válido e incidencias.
        // val students = mutableListOf<Student>()
        // val issues = mutableListOf<FileIssue>()

        // 3. Recorrer cada fichero y delegar el parseo en un objeto parser.
        // for (file in inputFiles) {
        //     val student = studentParser.parse(file)
        //     ...
        // }

        // 4. Si el parser detecta errores, guardar incidencias.
        // issues.add(FileIssue(...))

        // 5. Si el parser obtiene un alumno correcto, guardarlo como objeto `Student`.
        // students.add(student)

        // 6. Delegar la generación del correo del instituto a otra clase.
        // val institutionalEmail = emailGenerator.generate(student)

        // 7. Delegar la asignación de grupos a una clase especializada.
        // val assignment = groupAssigner.assign(student, currentGroups)

        // 8. Delegar la escritura de ficheros de salida a un escritor.
        // outputWriter.writeEmails(...)
        // outputWriter.writeGroups(...)

        // 9. Delegar el movimiento a `procesados` a un repositorio o gestor de ficheros.
        // studentFileRepository.moveToProcessed(file)

        // 10. Finalmente, construir un resumen y mostrarlo por consola.
        // val summary = ProcessingSummary(...)
        // summaryPrinter.print(summary)

        printSuggestedDesign()
    }

    /**
     * Muestra por consola una posible descomposición del problema en objetos.
     *
     * Esta salida sirve como guía para el alumnado: no es parte de la solución
     * final, sino una ayuda para entender cómo repartir responsabilidades.
     */
    private fun printSuggestedDesign() {
        println()
        println("Sugerencia de diseño orientado a objetos:")
        println("- StudentFileRepository: localiza, lee y mueve ficheros.")
        println("- StudentParser: convierte un fichero en un objeto Student.")
        println("- InstitutionalEmailGenerator: genera el correo del instituto.")
        println("- GroupAssigner: decide a qué grupo va cada alumno.")
        println("- OutputWriter: escribe los ficheros CSV y TXT.")
        println("- SummaryPrinter: muestra el resumen final.")
    }

    // Los siguientes métodos no se implementan todavía.
    // Se dejan como referencia para que el alumnado vea qué responsabilidades
    // podrían existir en una solución orientada a objetos.

    private fun findInputFiles(directory: Path): List<StudentFile> = emptyList()

    private fun registerIssue(fileName: String, message: String): FileIssue =
        FileIssue(fileName, message)

    private fun buildSummary(): ProcessingSummary =
        ProcessingSummary(
            detectedFiles = 0,
            validStudents = 0,
            issues = emptyList(),
        )
}
