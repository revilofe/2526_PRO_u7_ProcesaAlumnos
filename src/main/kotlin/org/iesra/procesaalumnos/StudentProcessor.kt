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

class StudentProcessor(private val random: Random = Random.Default) {
    fun process(courseGroup: String, directory: Path): Summary {
        require(directory.exists() && directory.isDirectory()) { "Directorio no válido: $directory" }
        val files = directory.toFile().listFiles { f -> f.isFile && f.extension.equals("txt", true) }
            ?.map { it.toPath() }?.sortedBy { it.name }.orEmpty()
        val processedDir = directory.resolve("procesados").also { it.createDirectories() }
        val validStudents = mutableListOf<Student>()
        val issues = mutableListOf<Issue>()

        files.forEach { file ->
            parseStudent(file).onSuccess(validStudents::add).onFailure {
                issues += Issue(file.name, it.message ?: "Formato inválido")
            }
            file.moveTo(processedDir.resolve(file.name), overwrite = true)
        }

        val assignments = assignGroups(validStudents, issues)
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

    private fun parseStudent(file: Path): Result<Student> = runCatching {
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

    private fun assignGroups(students: List<Student>, issues: MutableList<Issue>): List<AssignedStudent> {
        val groups = mutableMapOf("A" to mutableListOf<Student>(), "B" to mutableListOf(), "C" to mutableListOf(), "D" to mutableListOf())
        val result = mutableListOf<AssignedStudent>()

        students.forEach { student ->
            val requested = student.requestedGroup?.trim()?.uppercase().orEmpty()
            val chosen = when {
                requested.matches(Regex("[A-Z]")) && groups.getOrPut(requested) { mutableListOf() }.size < 5 -> requested
                else -> pickAvailableGroup(groups)
            }
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

    private fun pickAvailableGroup(groups: MutableMap<String, MutableList<Student>>): String {
        val free = groups.filterValues { it.size < 5 }.keys.sorted()
        if (free.isNotEmpty()) return free.random(random)
        val next = ((groups.keys.max().single()) + 1).toString()
        groups[next] = mutableListOf()
        return next
    }

    private fun buildInstituteEmail(student: Student): String {
        val parts = student.surnames.split(Regex("\\s+")).filter(String::isNotBlank).map(::normalize)
        require(parts.size >= 2) { "Los apellidos de ${student.fileName} deben incluir al menos dos apellidos." }
        val secondSurname = parts[1]
        require(secondSurname.length >= 2) { "El segundo apellido de ${student.fileName} debe tener al menos dos letras." }
        return "${normalize(student.name).first()}$secondSurname${secondSurname[1]}@iesrafaelalberti.es".lowercase()
    }

    private fun writeEmails(file: Path, rows: List<EmailRow>) {
        file.writeText(buildString {
            appendLine("nombre|apellidos|email1|email2")
            rows.forEach { appendLine("${it.name}|${it.surnames}|${it.email1}|${it.email2}") }
        })
    }

    private fun writeGroups(file: Path, rows: List<AssignedStudent>) {
        file.writeText(
            rows.groupBy { it.group }.toSortedMap().entries.joinToString("\n\n") { (group, students) ->
                "Grupo $group\n" + students.sortedBy { it.student.fullName }.joinToString("\n") { "- ${it.student.fullName}" }
            },
        )
    }

    private fun normalize(value: String): String =
        Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
            .replace(Regex("[^A-Za-z]"), "")
}

data class Student(val fileName: String, val name: String, val surnames: String, val email: String, val requestedGroup: String?) {
    val fullName = "$name $surnames"
}

data class AssignedStudent(val student: Student, val group: String)
data class EmailRow(val name: String, val surnames: String, val email1: String, val email2: String)
data class Issue(val fileName: String, val message: String)
data class Summary(
    val totalFiles: Int,
    val processedFiles: Int,
    val emails: List<EmailRow>,
    val groups: Map<String, List<String>>,
    val issues: List<Issue>,
)
