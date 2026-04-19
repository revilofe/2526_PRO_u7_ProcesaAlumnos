package org.iesra.procesaalumnos

import java.nio.file.Path
import java.text.Normalizer
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.io.path.moveTo
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.random.Random

/**
 * Implementación compacta del ejercicio.
 *
 * Reúne en una sola clase la lectura de ficheros, la validación, la asignación
 * de grupos y la generación de salidas para mantener una solución breve.
 *
 * @property random generador aleatorio usado al reasignar grupos.
 */
class StudentProcessor(private val random: Random = Random.Default) {
    /**
     * Procesa todos los ficheros `.txt` de un directorio y genera los ficheros de salida.
     *
     * @param courseGroup nombre del grupo principal, por ejemplo `DAW1`.
     * @param directory carpeta donde están los ficheros del alumnado.
     * @return resumen final del procesamiento.
     * @throws IllegalArgumentException si el directorio no existe o no es una carpeta.
     */
    fun process(courseGroup: String, directory: Path): Summary {
        require(directory.exists() && directory.isDirectory()) { "Directorio no válido: $directory" }
        val files = directory.toFile().listFiles { f -> f.isFile && f.extension.equals("txt", true) }
            ?.map { it.toPath() }?.sortedBy { it.name }.orEmpty()
        val processedDir = directory.resolve("procesados").also { it.createDirectories() }
        val validStudents = mutableListOf<Student>()
        val issues = mutableListOf<Issue>()

        files.forEach { file ->
            // Aunque el fichero sea incorrecto, se registra la incidencia y se mueve
            // igualmente a `procesados` para no volver a tratarlo en la siguiente ejecución.
            parseStudent(file).onSuccess(validStudents::add).onFailure {
                issues += Issue(file.name, it.message ?: "Formato inválido")
            }
            file.moveTo(processedDir.resolve(file.name), overwrite = true)
        }

        val assignments = assignGroups(validStudents, issues)
        // El correo institucional se genera después del parseo porque necesita
        // trabajar solo con alumnado ya validado.
        val emails = assignments.map { EmailRow(it.student.name, it.student.surnames, it.student.email, buildInstituteEmail(it.student)) }
        writeEmails(directory.resolve("$courseGroup-correos.csv"), emails)
        writeGroups(directory.resolve("$courseGroup-grupos.txt"), assignments)

        return Summary(
            totalFiles = files.size,
            processedFiles = validStudents.size,
            emails = emails,
            groups = assignments.groupBy { it.group }.mapValues { (_, v) -> v.map { it.student.fullName } },
            issues = issues.sortedBy(Issue::fileName),
        )
    }

    /**
     * Lee un fichero individual y extrae sus campos.
     *
     * El parser es intencionadamente corto: convierte cada línea `clave: valor`
     * en un mapa y luego valida los campos obligatorios.
     *
     * @param file fichero a interpretar.
     * @return el alumno leído o el error producido durante el parseo.
     */
    private fun parseStudent(file: Path): Result<Student> = runCatching {
        // Esta tubería transforma las líneas del fichero en un mapa:
        // 1. elimina espacios sobrantes
        // 2. ignora líneas vacías
        // 3. extrae pares clave/valor con una única expresión regular
        // 4. normaliza la clave a minúsculas para admitir `Nombre`, `nombre`, `EMAIL`, etc.
        val values = file.readText().lineSequence()
            .map(String::trim)
            .filter(String::isNotBlank)
            .mapNotNull { Regex("""^([A-Za-z]+)\s*[:=]\s*(.*)$""").matchEntire(it)?.destructured?.toList() }
            .associate { it[0].lowercase() to it[1].trim() }

        val name = values["nombre"] ?: error("Falta el campo obligatorio 'Nombre'.")
        val surnames = values["apellidos"] ?: error("Falta el campo obligatorio 'Apellidos'.")
        val email = values["email"] ?: error("Falta el campo obligatorio 'email'.")
        require(Regex("""^[a-z0-9._%+\-]+@g\.educaand\.es$""", RegexOption.IGNORE_CASE).matches(email)) {
            "El correo '$email' no tiene un formato válido de la Junta de Andalucía."
        }
        require("${email.substringBefore('@')}.txt" == file.name) {
            "El nombre del fichero no coincide con la parte local del correo. Esperado: ${email.substringBefore('@')}.txt."
        }
        Student(file.name, name, surnames, email, values["grupo"])
    }

    /**
     * Reparte al alumnado en grupos de 5.
     *
     * Si el grupo pedido no es válido o está lleno, busca otro con plazas y, si no
     * existe ninguno, crea el siguiente grupo del abecedario.
     *
     * @param students alumnado válido leído desde los ficheros.
     * @param issues lista mutable donde se añaden incidencias de reasignación.
     * @return asignaciones finales de cada alumno.
     */
    private fun assignGroups(students: List<Student>, issues: MutableList<Issue>): List<AssignedStudent> {
        val groups = mutableMapOf("A" to mutableListOf<Student>(), "B" to mutableListOf(), "C" to mutableListOf(), "D" to mutableListOf())
        val result = mutableListOf<AssignedStudent>()

        students.forEach { student ->
            val requested = student.requestedGroup?.trim()?.uppercase().orEmpty()
            // Si el grupo pedido es correcto y tiene hueco, se respeta.
            // En cualquier otro caso se busca uno alternativo.
            val chosen = when {
                requested.matches(Regex("[A-Z]")) && groups.getOrPut(requested) { mutableListOf() }.size < 5 -> requested
                else -> pickAvailableGroup(groups)
            }
            // El motivo se guarda como texto para reutilizarlo directamente
            // en el resumen de incidencias mostrado por consola.
            val reason = when {
                requested.isBlank() -> "grupo no informado, asignado aleatoriamente a $chosen"
                !requested.matches(Regex("[A-Z]")) -> "grupo no válido, asignado aleatoriamente a $chosen"
                requested != chosen -> "grupo solicitado lleno, asignado aleatoriamente a $chosen"
                else -> null
            }
            groups.getValue(chosen) += student
            result += AssignedStudent(student, chosen)
            if (reason != null) issues += Issue(student.fileName, reason)
        }
        return result
    }

    /**
     * Devuelve un grupo con plazas libres o crea el siguiente grupo del abecedario.
     *
     * @param groups estado actual de ocupación de los grupos.
     * @return nombre del grupo elegido para el siguiente alumno.
     */
    private fun pickAvailableGroup(groups: MutableMap<String, MutableList<Student>>): String {
        val free = groups.filterValues { it.size < 5 }.keys.sorted()
        if (free.isNotEmpty()) return free.random(random)
        val next = ((groups.keys.max().single()) + 1).toString()
        groups[next] = mutableListOf()
        return next
    }

    /**
     * Genera el correo institucional usando la regla del enunciado.
     *
     * @param student alumno ya validado.
     * @return correo del instituto en minúsculas.
     * @throws IllegalArgumentException si faltan dos apellidos o el segundo es demasiado corto.
     */
    private fun buildInstituteEmail(student: Student): String {
        val parts = student.surnames.split(Regex("\\s+")).filter(String::isNotBlank).map(::normalize)
        require(parts.size >= 2) { "Los apellidos de ${student.fileName} deben incluir al menos dos apellidos." }
        val secondSurname = parts[1]
        require(secondSurname.length >= 2) { "El segundo apellido de ${student.fileName} debe tener al menos dos letras." }
        return "${normalize(student.name).first()}$secondSurname${secondSurname[1]}@iesrafaelalberti.es".lowercase()
    }

    /**
     * Escribe el CSV de correos con el separador `|`.
     */
    private fun writeEmails(file: Path, rows: List<EmailRow>) {
        file.writeText(buildString {
            appendLine("nombre|apellidos|email1|email2")
            rows.forEach { appendLine("${it.name}|${it.surnames}|${it.email1}|${it.email2}") }
        })
    }

    /**
     * Escribe el fichero de grupos agrupando primero por letra de grupo y luego por nombre.
     */
    private fun writeGroups(file: Path, rows: List<AssignedStudent>) {
        file.writeText(
            rows.groupBy { it.group }.toSortedMap().entries.joinToString("\n\n") { (group, students) ->
                "Grupo $group\n" + students.sortedBy { it.student.fullName }.joinToString("\n") { "- ${it.student.fullName}" }
            },
        )
    }

    /**
     * Elimina tildes y caracteres no alfabéticos para poder construir el correo.
     */
    private fun normalize(value: String): String =
        Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
            .replace(Regex("[^A-Za-z]"), "")
}

/**
 * Alumno leído desde un fichero de entrada.
 */
data class Student(val fileName: String, val name: String, val surnames: String, val email: String, val requestedGroup: String?) {
    /**
     * Nombre completo listo para mostrar en salidas.
     */
    val fullName = "$name $surnames"
}

/**
 * Relación entre un alumno y el grupo final asignado.
 */
data class AssignedStudent(val student: Student, val group: String)

/**
 * Fila del CSV de correos.
 */
data class EmailRow(val name: String, val surnames: String, val email1: String, val email2: String)

/**
 * Incidencia detectada durante el procesamiento de un fichero.
 */
data class Issue(val fileName: String, val message: String)

/**
 * Resumen final mostrado por consola y útil para tests.
 */
data class Summary(
    val totalFiles: Int,
    val processedFiles: Int,
    val emails: List<EmailRow>,
    val groups: Map<String, List<String>>,
    val issues: List<Issue>,
)
