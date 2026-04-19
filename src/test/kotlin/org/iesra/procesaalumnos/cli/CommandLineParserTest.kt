package org.iesra.procesaalumnos.cli

import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CommandLineParserTest {
    private val parser = CommandLineParser()

    @Test
    fun `parsea grupo obligatorio y path opcional`() {
        val options = parser.parse(arrayOf("--grupo", "DAW1", "--path", "datos"))
        assertEquals("DAW1", options.courseGroup)
        assertEquals(Path("datos"), options.inputDirectory)
    }

    @Test
    fun `falla cuando falta grupo`() {
        val exception = assertFailsWith<CliException> {
            parser.parse(arrayOf("--path", "datos"))
        }
        assertEquals(true, exception.message!!.contains("--grupo"))
    }
}
