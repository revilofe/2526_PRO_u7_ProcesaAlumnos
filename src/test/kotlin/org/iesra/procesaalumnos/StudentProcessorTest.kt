package org.iesra.procesaalumnos

import kotlin.io.path.createTempDirectory
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StudentProcessorTest {
    @Test
    fun `procesa ficheros genera salidas y crea grupos nuevos si hace falta`() {
        val dir = createTempDirectory()
        listOf("A", "B", "C", "D").forEach { group ->
            (1..5).forEach { index ->
                val id = "${group.lowercase()}$index"
                dir.resolve("$id.txt").writeText(
                    "Nombre: ${group}Alumno$index\nApellidos: Lopez Perez\nemail: $id@g.educaand.es\nGrupo = $group",
                )
            }
        }
        dir.resolve("extra.txt").writeText("Nombre: Extra\nApellidos: Lopez Perez\nemail: extra@g.educaand.es\nGrupo = A")
        dir.resolve("bad.txt").writeText("Nombre: X\nemail: bad@g.educaand.es\nGrupo = B")

        val summary = StudentProcessor(Random(0)).process("DAW1", dir)

        assertEquals(22, summary.totalFiles)
        assertEquals(21, summary.processedFiles)
        assertEquals(21, summary.emails.size)
        assertEquals(5, summary.groups.getValue("A").size)
        assertEquals(1, summary.groups.getValue("E").size)
        assertEquals(2, summary.issues.count())
        assertTrue(dir.resolve("DAW1-correos.csv").exists())
        assertTrue(dir.resolve("DAW1-grupos.txt").exists())
        assertTrue(dir.resolve("procesados/extra.txt").exists())
    }

    @Test
    fun `escribe el csv con el formato esperado`() {
        val dir = createTempDirectory()
        dir.resolve("eferoli398.txt").writeText(
            "Nombre: Eduardo\nApellidos: Fernandez Oliver\nemail: eferoli398@g.educaand.es\nGrupo = A",
        )

        StudentProcessor(Random(0)).process("DAW1", dir)

        val csv = dir.resolve("DAW1-correos.csv").readText()
        assertTrue(csv.contains("nombre|apellidos|email1|email2"))
        assertTrue(csv.contains("Eduardo|Fernandez Oliver|eferoli398@g.educaand.es|eoliverl@iesrafaelalberti.es"))
    }
}
