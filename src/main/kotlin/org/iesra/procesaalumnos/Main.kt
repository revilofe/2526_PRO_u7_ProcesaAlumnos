package org.iesra.procesaalumnos

import org.iesra.procesaalumnos.application.StudentProcessingService
import org.iesra.procesaalumnos.cli.CommandLineParser
import org.iesra.procesaalumnos.cli.ConsoleSummaryPrinter
import org.iesra.procesaalumnos.domain.service.DefaultInstitutionalEmailGenerator
import org.iesra.procesaalumnos.domain.service.RandomGroupAllocator
import org.iesra.procesaalumnos.infrastructure.fs.FileSystemStudentRepository
import org.iesra.procesaalumnos.infrastructure.fs.FileSystemWriter
import org.iesra.procesaalumnos.infrastructure.parsing.PlainTextStudentFileParser
import kotlin.random.Random

fun main(args: Array<String>) {
    val parser = CommandLineParser()
    val options = parser.parse(args)

    val service = StudentProcessingService(
        repository = FileSystemStudentRepository(),
        studentFileParser = PlainTextStudentFileParser(),
        emailGenerator = DefaultInstitutionalEmailGenerator(),
        groupAllocator = RandomGroupAllocator(random = Random.Default),
        writer = FileSystemWriter(),
    )

    val summary = service.process(options)
    ConsoleSummaryPrinter().print(summary)
}
