package org.iesra.procesaalumnos.infrastructure.parsing

import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import org.iesra.procesaalumnos.domain.model.ParseStudentResult

class PlainTextStudentFileParserTest {
    private val parser = PlainTextStudentFileParser()

    @Test
    fun `parsea correctamente un fichero valido`() {
        val result = parser.parse(
            Path("eferoli398.txt"),
            """
            Nombre: Eduardo
            Apellidos: Fernandez Oliver
            email: eferoli398@g.educaand.es
            Grupo = A
            """.trimIndent(),
        )
        val success = assertIs<ParseStudentResult.Success>(result)
        assertEquals("Eduardo", success.student.name)
        assertEquals("Fernandez Oliver", success.student.surnames)
        assertEquals("A", success.student.requestedGroupRaw)
    }

    @Test
    fun `rechaza correos con formato invalido`() {
        val result = parser.parse(
            Path("xbadmail555.txt"),
            """
            Nombre: Ximena
            Apellidos: Baena Duran
            email: xbadmail555#g.educaand.es
            Grupo = D
            """.trimIndent(),
        )
        val failure = assertIs<ParseStudentResult.Failure>(result)
        assertEquals(true, failure.issue.message.contains("formato válido"))
    }

    @Test
    fun `rechaza nombre de fichero que no coincide con el correo`() {
        val result = parser.parse(
            Path("nomatch999.txt"),
            """
            Nombre: Oscar
            Apellidos: Medina Torres
            email: omedina999@g.educaand.es
            Grupo = D
            """.trimIndent(),
        )
        val failure = assertIs<ParseStudentResult.Failure>(result)
        assertEquals(true, failure.issue.message.contains("no coincide"))
    }
}
