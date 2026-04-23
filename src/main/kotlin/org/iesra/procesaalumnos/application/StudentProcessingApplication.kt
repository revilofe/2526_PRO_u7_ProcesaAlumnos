package org.iesra.procesaalumnos.application

import org.iesra.procesaalumnos.cli.CliOptions

/**
 * Coordina el caso de uso principal del programa.
 *
 * En esta base didáctica se comporta como orquestador: recibe los datos de entrada
 * ya parseados y explica cómo se podría repartir el trabajo entre otros objetos.
 *
 * Importante: en la rama `main` todavía no depende de implementaciones reales ni de
 * interfaces con métodos, porque la intención aquí es enseñar la estructura antes
 * de construir la solución completa.
 */
class StudentProcessingApplication {

    /**
     * Ejecuta el flujo principal del programa.
     *
     * @param options opciones recibidas desde la línea de comandos.
     */
    fun run(options: CliOptions) {
        println("Grupo recibido: ${options.group}")
        println("Directorio de trabajo: ${options.path}")

        // A partir de aquí, una solución OO razonable podría seguir este flujo.
        // En esta rama base no se implementa todavía: solo se deja la guía.

        // ####################### Entrada: Lectura de datos, conversión a estructuras

        // 1. Pedir a una clase repositorio que localice los `.txt` de entrada.
        // val inputFiles = studentFileRepository.findInputFiles(options.path)

        // 2. Crear colecciones donde guardar alumnado válido e incidencias.
        // val students = mutableListOf<Student>()
        // val issues = mutableListOf<FileIssue>()

        // 3. Recorrer cada fichero y delegar el parseo en un objeto parser.
        // for (file in inputFiles) {
        //     val student = studentParser.parse(file)
        //     ...
               // 4. Si el parser detecta errores, guardar incidencias.
               // issues.add(FileIssue(...))

               // 5. Si el parser obtiene un alumno correcto, guardarlo como objeto `Student`.
               // students.add(student)

               // 6. Delegar el movimiento a `procesados` a un repositorio o gestor de ficheros.
               // studentFileRepository.moveToProcessed(file)


        // }

        // ####################### Procesamiento: de datos de entrada, y generación de datos de salida

        // 7. Para cada Student, delegar la generación del correo del instituto a otra clase.
        // val institutionalEmail = emailGenerator.generate(student)

        // 8. Para cada Student, delegar la asignación de grupos a una clase especializada.
        // val assignment = groupAssigner.assign(student, currentGroups)

        // ####################### Salida: ficheros de salida y resumen

        // 9. Delegar la escritura de ficheros de salida a un escritor.
        // outputWriter.writeEmails(...)
        // outputWriter.writeGroups(...)


        // 10. Finalmente, construir un resumen y mostrarlo por consola.
        // val summary = ProcessingSummary(...)
        // summaryPrinter.print(summary)

        printSuggestedDesign()
    }

    /**
     * Muestra por consola una posible descomposición del problema en objetos.
     *
     * Esta salida sirve como orientación y no forma parte de la solución final.
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
}
