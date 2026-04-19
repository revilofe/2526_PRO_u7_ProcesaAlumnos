package org.iesra.procesaalumnos.domain.service

import java.text.Normalizer
import org.iesra.procesaalumnos.domain.model.StudentRequest
import org.iesra.procesaalumnos.domain.port.InstitutionalEmailGenerator

/**
 * Genera correos institucionales siguiendo la regla del enunciado.
 */
class DefaultInstitutionalEmailGenerator : InstitutionalEmailGenerator {
    override fun generate(student: StudentRequest): String {
        val normalizedName = normalizeToken(student.name)
        require(normalizedName.isNotBlank()) {
            "El nombre de ${student.fileName} no permite generar el correo institucional."
        }

        val surnameParts = student.surnames.trim().split(Regex("\\s+")).filter(String::isNotBlank).map(::normalizeToken)
        require(surnameParts.size >= 2) {
            "Los apellidos de ${student.fileName} deben incluir al menos dos apellidos."
        }

        val secondSurname = surnameParts[1]
        require(secondSurname.length >= 2) {
            "El segundo apellido de ${student.fileName} debe tener al menos dos letras."
        }

        return buildString {
            append(normalizedName.first())
            append(secondSurname)
            append(secondSurname[1])
            append(DOMAIN)
        }.lowercase()
    }

    private fun normalizeToken(raw: String): String =
        Normalizer.normalize(raw.trim(), Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
            .replace(Regex("[^A-Za-z]"), "")

    private companion object {
        const val DOMAIN = "@iesrafaelalberti.es"
    }
}
