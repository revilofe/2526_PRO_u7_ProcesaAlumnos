# Casos de prueba

Conjunto de 26 ficheros para validar lectura, escritura, gestión de errores, reasignación de grupos y movimiento a `procesados`.

## Casos cubiertos

- Ficheros válidos con grupos `A`, `B`, `C` y `D`.
- Grupos `A`, `B` y `C` completos para forzar reasignaciones.
- Grupo vacío.
- Grupo no válido.
- Correo mal formado.
- Nombre del fichero que no coincide con la parte local del correo.
- Fichero con campo obligatorio ausente.
- Variante con espacios adicionales y mezcla de mayúsculas/minúsculas.

## Distribución intencionada

- `A`: 5 solicitudes válidas y 2 adicionales para provocar grupo lleno.
- `B`: 5 solicitudes válidas y 1 adicional para provocar grupo lleno.
- `C`: 5 solicitudes válidas y 1 adicional para provocar grupo lleno.
- `D`: varias plazas libres para absorber reasignaciones.
