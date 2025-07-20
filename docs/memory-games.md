# Diagrama de Flujo del Juego de Memoria

El siguiente diagrama de flujo describe el ciclo de vida de un juego de memoria basado en la clase `MemoryGame`.

```mermaid
flowchart TD
    A["Estado Inicial: WAITING_TO_START\n(Juego en espera de inicio)"]
    B["startGame()\n- Registra startTime\n- Cambia estado a SHOWING_PIECES\n- Llama a loadExercise()"]
    C["loadExercise()\n- Carga el FEN actual\n- Actualiza el tablero\n- Oculta piezas\n- Registra partialTime"]
    D["Mostrar Piezas\n(Se visualizan durante un tiempo determinado)"]
    E["isTimeToHidePieces()\n- Verifica si se cumplió el tiempo de visualización\n- Cambia estado a GUESSING_PIECES si es verdadero"]
    F["Esperar Respuesta del Usuario\n(Usuario ingresa su respuesta)"]
    G["submitAnswer(answer)\n- Evalúa la respuesta\n- Actualiza estadísticas y tiempos"]
    H["nextExercise()\n- Si hay más ejercicios: incrementa índice y llama a loadExercise()\n- Si no: cambia estado a GAME_OVER"]
    I["Fin del Juego\n(GAME_OVER)"]

    A --> B
    B --> C
    C --> D
    D --> E
    E --> F
    F --> G
    G --> H
    H -- "Más ejercicios" --> C
    H -- "Sin ejercicios" --> I
```
* Estado Inicial (WAITING_TO_START):
El juego comienza en un estado de espera. Se inicia al llamar a startGame().
* startGame():
	•	Registra el tiempo de inicio.
	•	Cambia el estado a SHOWING_PIECES.
	•	Invoca loadExercise() para cargar el primer ejercicio.
* loadExercise():
	•	Selecciona el FEN correspondiente al ejercicio actual.
	•	Actualiza el tablero y oculta las piezas según la lógica del juego.
	•	Registra el tiempo parcial (partialTime).
* Mostrar Piezas:
 Se muestran las piezas en el tablero durante un tiempo predefinido, permitiendo al usuario memorizarlas.
* isTimeToHidePieces():
	•	Verifica si el tiempo de visualización ha transcurrido.
	•	Si es así, cambia el estado a GUESSING_PIECES para que el usuario pueda enviar su respuesta.
* Esperar Respuesta del Usuario:
El juego espera que el usuario ingrese su respuesta (por ejemplo, adivinar las piezas ocultas o introducir un movimiento).
* submitAnswer(answer):
	•	Evalúa la respuesta recibida.
	•	Actualiza las estadísticas y tiempos del juego.
	•	La respuesta puede ser de diferentes tipos, según el juego.
* nextExercise():
	•	Si existen más ejercicios, incrementa el índice y vuelve a cargar el siguiente ejercicio.
	•	Si no hay más ejercicios, cambia el estado a GAME_OVER.
* Fin del Juego (GAME_OVER):
Se alcanza cuando ya no hay más ejercicios disponibles, finalizando el ciclo del juego.