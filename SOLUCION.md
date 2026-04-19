# Solución Simple

## Idea general

La rama `simple` contiene una solución deliberadamente compacta. El objetivo no es separar el programa en muchas capas, sino resolver el ejercicio con pocas clases y con una estructura fácil de ejecutar y modificar.

La solución se apoya en dos archivos principales:

- `Main.kt`: entrada del programa y lectura de argumentos.
- `StudentProcessor.kt`: toda la lógica del ejercicio.

## Flujo de ejecución

1. `main` recibe los argumentos `--grupo` y `--path`.
2. `parseArgs` convierte esos argumentos en un objeto `CliOptions`.
3. `StudentProcessor.process(...)` busca todos los `.txt` del directorio indicado.
4. Cada fichero se intenta leer y validar con `parseStudent`.
5. Si el fichero es correcto:
   - se crea un objeto `Student`
   - se guarda para procesarlo más tarde
6. Si el fichero es incorrecto:
   - se registra una incidencia
7. En ambos casos, el fichero se mueve a `procesados`.
8. Con los alumnos válidos:
   - se asignan grupos con `assignGroups`
   - se generan los correos del instituto con `buildInstituteEmail`
9. Se escriben dos salidas:
   - `<grupo>-correos.csv`
   - `<grupo>-grupos.txt`
10. Se devuelve un `Summary` con toda la información del proceso.

## Estructuras principales

### `Student`

Representa un alumno leído desde un fichero:

- nombre del fichero
- nombre
- apellidos
- email original
- grupo solicitado

### `AssignedStudent`

Guarda el alumno y el grupo final que se le ha asignado.

### `EmailRow`

Representa una línea del CSV de correos.

### `Issue`

Representa un problema detectado durante el procesamiento.

### `Summary`

Resume el resultado final:

- número total de ficheros
- número de ficheros procesados correctamente
- correos generados
- grupos finales
- incidencias

## Métodos importantes

### `process`

Es el método principal. Coordina todo:

- valida el directorio
- localiza ficheros
- parsea alumnos
- mueve ficheros
- asigna grupos
- genera correos
- escribe salidas
- construye el resumen final

### `parseStudent`

Lee un fichero y convierte sus líneas en un mapa `clave -> valor`.

Ejemplo:

```text
Nombre: Eduardo
Apellidos: Fernandez Oliver
email: eferoli398@g.educaand.es
Grupo = A
```

Se transforma internamente en algo parecido a:

```text
nombre -> Eduardo
apellidos -> Fernandez Oliver
email -> eferoli398@g.educaand.es
grupo -> A
```

Después valida:

- que existan `nombre`, `apellidos` y `email`
- que el correo tenga formato válido
- que el nombre del fichero coincida con la parte local del correo

### `assignGroups`

Recorre el alumnado válido y decide el grupo final.

Reglas:

- si el grupo pedido es válido y tiene plazas, se respeta
- si no, se busca otro grupo con plazas
- si todos están llenos, se crea el siguiente grupo del abecedario

Además, si hay reasignación, se añade una incidencia explicando el motivo.

### `pickAvailableGroup`

Hace dos cosas:

- intenta elegir un grupo existente con menos de 5 alumnos
- si no existe ninguno, crea el siguiente grupo (`E`, `F`, `G`, ...)

### `buildInstituteEmail`

Genera el correo interno con la regla del enunciado:

- primera letra del nombre
- segundo apellido
- segunda letra del segundo apellido
- dominio `@iesrafaelalberti.es`

Antes normaliza el texto para quitar tildes y caracteres no válidos.

## Por qué esta solución es "simple"

Se considera simple porque:

- casi toda la lógica está concentrada en una sola clase
- no hay capas, puertos ni adaptadores separados
- no hay interfaces ni servicios intermedios
- los modelos son `data class` muy pequeños

Esto reduce el número de archivos y hace más fácil seguir el programa completo, aunque a cambio mezcla varias responsabilidades en el mismo fichero.

## Ventajas didácticas

- Es más fácil de ejecutar y depurar al principio.
- Permite ver el flujo completo del programa sin saltar entre muchos archivos.
- Es útil para alumnado que todavía no domina arquitecturas por capas.

## Limitaciones frente a la solución completa

- Mezcla lectura, validación, lógica de negocio y escritura.
- Es menos flexible para sustituir partes del programa.
- Escala peor si el ejercicio crece mucho.
- Tiene menos separación de responsabilidades.

## Relación con los tests

Los tests comprueban:

- que el procesamiento genera salidas
- que se crean grupos nuevos cuando hace falta
- que el CSV tiene el formato esperado

La idea es validar el comportamiento observable del programa sin fragmentarlo en demasiadas piezas.
