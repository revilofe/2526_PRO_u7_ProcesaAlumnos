package org.iesra.procesaalumnos.domain.port

import org.iesra.procesaalumnos.domain.model.StudentRequest

fun interface InstitutionalEmailGenerator {
    fun generate(student: StudentRequest): String
}
