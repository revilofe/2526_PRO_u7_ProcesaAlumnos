package org.iesra.procesaalumnos.infrastructure.parsing

import java.nio.file.Path
import kotlin.io.path.name
import org.iesra.procesaalumnos.domain.model.FileIssue
import org.iesra.procesaalumnos.domain.model.ParseStudentResult
import org.iesra.procesaalumnos.domain.model.StudentRequest
import org.iesra.procesaalumnos.domain.port.StudentFileParser

/**
 * Parser tolerante con espacios extra y diferencias de mayúsculas/minúsculas.
 */
class PlainTextStudentFileParser : StudentFileParser {
    override fun parse(file: Path, content: String): ParseStudentResult {
        val values = content.lineSequence()
            .map(String::trim)
            .filter(String::isNotBlank)
            .mapNotNull(::parseField)
            .toMap()

        val name = values["nombre"] ?: return failure(file, "Falta el campo obligatorio 'Nombre'.")
        val surnames = values["apellidos"] ?: return failure(file, "Falta el campo obligatorio 'Apellidos'.")
        val email = values["email"] ?: return failure(file, "Falta el campo obligatorio 'email'.")

        if (!EMAIL_REGEX.matches(email)) {
            return failure(file, "El correo '$email' no tiene un formato válido de la Junta de Andalucía.")
        }

        val expectedFileName = email.substringBefore('@') + TXT_EXTENSION
        if (file.name != expectedFileName) {
            return failure(file, "El nombre del fichero no coincide con la parte local del correo. Esperado: $expectedFileName.")
        }

        return ParseStudentResult.Success(
            StudentRequest(
                sourceFile = file,
                fileName = file.name,
                name = name,
                surnames = surnames,
                externalEmail = email,
                requestedGroupRaw = values["grupo"],
            ),
        )
    }

    private fun parseField(line: String): Pair<String, String>? {
        val match = FIELD_REGEX.matchEntire(line) ?: return null
        return match.groupValues[1].trim().lowercase() to match.groupValues[2].trim()
    }

    private fun failure(file: Path, message: String): ParseStudentResult.Failure =
        ParseStudentResult.Failure(FileIssue(file.name, message))

    private companion object {
        const val TXT_EXTENSION = ".txt"
        val FIELD_REGEX = Regex("""^([A-Za-z]+)\s*[:=]\s*(.*)$""")
        val EMAIL_REGEX = Regex("""^[a-z0-9._%+\-]+@g\.educaand\.es$""", RegexOption.IGNORE_CASE)
    }
}
