package org.iesra.procesaalumnos.cli

import org.iesra.procesaalumnos.domain.model.FileIssue
import org.iesra.procesaalumnos.domain.model.ProcessingSummary

/**
 * Presenta por consola el resumen final del procesamiento.
 */
class ConsoleSummaryPrinter(
    private val output: (String) -> Unit = ::println,
) {
    fun print(summary: ProcessingSummary) {
        output("Ficheros detectados: ${summary.discoveredFiles}")
        output("Ficheros procesados correctamente: ${summary.processedFiles}")
        output("Ficheros con errores: ${summary.issues.size}")
        output("Correos creados correctamente: ${summary.emailRecords.size}")
        output("")
        output("Resumen de grupos:")
        summary.groupMembersByGroup.forEach { (group, members) ->
            output("- Grupo ${group.value}: ${members.size} alumnos")
        }
        if (summary.groupMembersByGroup.isNotEmpty()) {
            output("")
            output("Integrantes por grupo:")
            summary.groupMembersByGroup.forEach { (group, members) ->
                output("Grupo ${group.value}")
                members.forEach { output("- ${it.student.fullName}") }
            }
        }
        if (summary.issues.isNotEmpty()) {
            output("")
            output("Incidencias:")
            summary.issues.forEach(::printIssue)
        }
    }

    private fun printIssue(issue: FileIssue) {
        output("- archivo ${issue.fileName}: ${issue.message}")
    }
}
