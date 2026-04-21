package org.iesra.procesaalumnos.cli

import java.nio.file.Path

/**
 * Objeto de datos con los parámetros ya interpretados.
 *
 * Este tipo de clase suele llamarse DTO o Value Object,
 * porque solo transporta datos entre objetos.
 *
 * @property group identificador del grupo principal, por ejemplo `DAW1`.
 * @property path carpeta donde se buscarán los ficheros de entrada.
 */
data class CliOptions(
    val group: String,
    val path: Path,
)
