package org.iesra.procesaalumnos.domain.model

/**
 * Representa un subgrupo de alumnado como una letra única.
 */
@JvmInline
value class GroupCode private constructor(val value: String) {
    companion object {
        fun of(raw: String): GroupCode {
            val normalized = raw.trim().uppercase()
            require(normalized.matches(Regex("[A-Z]"))) {
                "El grupo debe ser una única letra entre A y Z: '$raw'"
            }
            return GroupCode(normalized)
        }

        fun fromOrNull(raw: String?): GroupCode? =
            raw?.takeIf { it.isNotBlank() }?.let { runCatching { of(it) }.getOrNull() }
    }
}
