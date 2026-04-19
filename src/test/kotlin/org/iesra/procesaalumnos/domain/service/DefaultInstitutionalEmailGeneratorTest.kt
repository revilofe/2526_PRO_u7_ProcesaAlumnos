package org.iesra.procesaalumnos.domain.service

import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.iesra.procesaalumnos.domain.model.StudentRequest

class DefaultInstitutionalEmailGeneratorTest {
    private val generator = DefaultInstitutionalEmailGenerator()

    @Test
    fun `genera correo institucional a partir del segundo apellido`() {
        val student = StudentRequest(
            sourceFile = Path("eferoli398.txt"),
            fileName = "eferoli398.txt",
            name = "Eduardo",
            surnames = "Fernandez Oliver",
            externalEmail = "eferoli398@g.educaand.es",
            requestedGroupRaw = "A",
        )
        val email = generator.generate(student)
        assertEquals("eoliverl@iesrafaelalberti.es", email)
    }

    @Test
    fun `falla cuando no hay segundo apellido`() {
        val student = StudentRequest(
            sourceFile = Path("test.txt"),
            fileName = "test.txt",
            name = "Eva",
            surnames = "Lopez",
            externalEmail = "eva@g.educaand.es",
            requestedGroupRaw = "A",
        )
        assertFailsWith<IllegalArgumentException> { generator.generate(student) }
    }
}
