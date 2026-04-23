# Entrega - Procesa Alumnos

> Sustituye todos los textos marcados como `TODO` por tu respuesta.
> Los enlaces al código deben ser enlaces permanentes de GitHub, no enlaces a una rama como `main`.

## Datos de la entrega

- Alumno/a: TODO
- Curso / grupo: TODO
- Repositorio de GitHub: TODO
- Commit usado para la entrega: TODO
- Lenguaje y versión utilizada: TODO

## Descripción breve de la solución

TODO: Explica en 5-10 líneas cómo has organizado tu programa y qué pasos sigue para procesar los ficheros de alumnado.

Ejemplo de aspectos que puedes comentar:

- Cómo recibe los argumentos `--grupo` y `--path`.
- Cómo localiza y lee los ficheros `.txt`.
- Cómo valida los datos de cada alumno/a.
- Cómo genera el correo del instituto.
- Cómo asigna los grupos.
- Cómo genera los ficheros de salida.
- Cómo mueve los ficheros procesados.

## EJECUCIÓN: Ejemplos

### Ejecución 1: usando el directorio actual

Comando ejecutado:

```bash
TODO
```

Salida obtenida por consola:

```text
TODO
```

### Ejecución 2: usando una ruta indicada con `--path`

Comando ejecutado:

```bash
TODO
```

Salida obtenida por consola:

```text
TODO
```


## SALIDA: Ficheros generados, salida por consola e incidencias y movimiento de ficheros

### Fichero de correos

Nombre del archivo: `TODO-correos.csv`

```text
TODO
```

### Fichero de grupos

Nombre del archivo: `TODO-grupos.txt`

```text
TODO
```

### Salida a consola con incidencias

```text
TODO
```

### Captura de la carpeta `procesados` con los ficheros movidos

TODO enlaces a la pantallka

## Respuestas a las preguntas de evaluación

### [CE 7.a] 1.a. Lectura de datos desde la consola y escritura de resultados por consola

Describe cómo has implementado la lectura de argumentos desde la consola y la escritura del resumen final por consola.

Respuesta:

TODO

Enlaces permanentes al código:

- Lectura de argumentos: TODO


### [CE 7.b] 2.a. Visualización de la información por consola

Describe cómo muestras la información por consola y qué formato has usado para que sea clara.

Respuesta:

TODO

Enlaces permanentes al código:

- Formato del resumen: TODO
- Formato de incidencias: TODO

### [CE 7.c] 3.a. Librerías o clases utilizadas

Indica qué librerías, clases o APIs has utilizado para realizar la práctica. Comenta los métodos más importantes.

Respuesta:

TODO

Clases, librerías o APIs utilizadas:

- TODO
- TODO
- TODO

Enlaces permanentes al código:

- Uso de clases/librerías principales: TODO

### [CE 7.d] 4.a. Formato de los ficheros de entrada y salida

Explica qué formato tienen los ficheros de entrada y qué formato tienen los ficheros de salida.

Respuesta:

TODO

Enlaces permanentes al código:

- Lectura del formato de entrada: TODO
- Generación del CSV con separador `|`: TODO
- Generación del fichero de grupos: TODO

### [CE 7.d] 4.b. Ventajas del formato elegido

Explica qué facilidades te ha dado usar ese formato para recuperar y guardar la información.

Respuesta:

TODO

### [CE 7.d] 4.c. Gestión de errores

Explica cómo gestionas los errores de formato, datos incompletos, errores con ficheros, grupos no indicados o grupos llenos.

Respuesta:

TODO

Enlaces permanentes al código:

- Validación de datos obligatorios: TODO
- Gestión de grupos no válidos o vacíos: TODO
- Errores con ficheros: TODO
- Registro de incidencias: TODO

### [CE 7.e] 5.a. Forma de acceso para leer información

Describe cómo accedes a los ficheros para leer la información de entrada.

Respuesta:

TODO

Enlaces permanentes al código:

- Acceso de lectura a ficheros: TODO

### [CE 7.e] 5.b. Forma de acceso para escribir información

Describe cómo accedes a los ficheros para escribir la información de salida.

Respuesta:

TODO

Enlaces permanentes al código:

- Escritura del fichero de correos: TODO
- Escritura del fichero de grupos: TODO

### [CE 7.e] 5.c. Forma de acceso para actualizar o mover información

Describe cómo actualizas o mueves información durante el procesamiento. Por ejemplo, el movimiento de ficheros a la carpeta `procesados`.

Respuesta:

TODO

Enlaces permanentes al código:

- Creación de la carpeta `procesados`: TODO
- Movimiento de ficheros procesados: TODO

## Casos de prueba realizados

Marca los casos que has probado e indica el resultado obtenido.

- [ ] Alumno/a con grupo válido y con plazas disponibles.
- [ ] Alumno/a sin grupo informado.
- [ ] Alumno/a con grupo lleno.
- [ ] Alumno/a con fichero mal formado.
- [ ] Generación de un nuevo grupo siguiendo el abecedario.
- [ ] Creación automática de la carpeta `procesados`.
- [ ] Generación correcta del fichero `<grupo>-correos.csv`.
- [ ] Generación correcta del fichero `<grupo>-grupos.txt`.

Comentarios sobre las pruebas:

TODO

## Problemas encontrados y soluciones aplicadas

Explica brevemente los principales problemas que has encontrado durante el desarrollo y cómo los has solucionado.

TODO

## Mejoras posibles

Indica qué mejorarías si tuvieras más tiempo.

TODO
