Feature: Registro de admisiones en urgencias
  Con el fin de que los pacientes sean atendidos cuanto antes por el médico según su nivel de prioridad
  Como enfermera
  Quiero poder registrar las admisiones de los pacientes a urgencias
#10 escenarios
  Background:
    Given la enfermera siguiente enfermera está autenticada en el sistema
    |Nombre|Apellido  |Cuil         |Matricula|
    |Maria |Del Valle |20-30574930-4|ABC123|

    And existe en el sistema el paciente:
      | cuil           | nombre      |
      | 20-44477310-4  | Enzo Juarez |
    And existen las prioridades de emergencia:
      | nivel | color     | descripcion         |
      | 1     | Rojo      | Crítica             |
      | 2     | Naranja   | Emergencia          |
      | 3     | Amarillo  | Urgencia            |
      | 4     | Verde     | Urgencia menor      |
      | 5     | Azul      | Sin urgencia        |

  Scenario: Registro exitoso de admisión de un paciente existente
    When registro el ingreso del paciente con los siguientes datos:
      | informe          | temperatura | frecuencia cardiaca | frecuencia respiratoria | frecuencia sistolica | frecuencia diastolica | nivel     |
      | Dolor torácico   | 38.2        | 92                  | 19                      | 120                  | 80                    | 5         |
    Then el ingreso queda registrado en el sistema
    And el estado inicial del ingreso es "PENDIENTE"
    And el sistema registra a la enfermera responsable en el ingreso
    And el paciente entra en la cola de atención

  # ──────────────── ESCENARIOS NEGATIVOS ────────────────

  Scenario Outline: Registro fallido porque el informe es nulo o está vacío
    When intento registrar el ingreso del paciente con los siguientes datos:
      | informe   | temperatura | frecuencia cardiaca | frecuencia respiratoria | frecuencia sistolica | frecuencia diastolica | nivel     |
      | <informe> | 37.5        | 80                  | 18                      | 120                  | 80                    | 3         |
    Then el sistema muestra un mensaje de error "El informe es obligatorio y no puede estar vacío ni contener solo espacios"

    Examples:
      | informe |
      | null    |
      | ""      |
      | "   "   |

  Scenario Outline: Registro fallido por temperatura inválida
    When intento registrar el ingreso del paciente con los siguientes datos:
      | informe         | temperatura   | frecuencia cardiaca | frecuencia respiratoria | frecuencia sistolica | frecuencia diastolica | nivel     |
      | Dolor abdominal | <temperatura> | 80                  | 18                      | 120                  | 80                    | 3         |
    Then el sistema muestra un mensaje de error "La temperatura debe ser un número válido en grados Celsius"

    Examples:
      | temperatura |
      | -36.5       |
      | texto       |
      | null        |

  Scenario Outline: Registro fallido por frecuencia cardíaca inválida
    When intento registrar el ingreso del paciente con los siguientes datos:
      | informe        | temperatura | frecuencia cardiaca       | frecuencia respiratoria | frecuencia sistolica | frecuencia diastolica | nivel |
      | Dolor torácico | 37.8        | <frecuencia cardiaca>     | 16                      | 120                  | 80                    | 5     |
    Then el sistema muestra un mensaje de error "La frecuencia cardíaca debe ser un número válido (latidos por minuto)"

    Examples:
      | frecuencia cardiaca |
      | -75                 |
      | texto               |
      | null                |

  Scenario Outline: Registro fallido por frecuencia respiratoria inválida
    When intento registrar el ingreso del paciente con los siguientes datos:
      | informe         | temperatura | frecuencia cardiaca | frecuencia respiratoria         | frecuencia sistolica | frecuencia diastolica | nivel   |
      | Cefalea intensa | 38.2        | 85                  | <frecuencia respiratoria>       | 125                  | 80                    | 4       |
    Then el sistema muestra un mensaje de error "La frecuencia respiratoria debe ser un número válido (respiraciones por minuto)"

    Examples:
      | frecuencia respiratoria |
      | -20                     |
      | texto                   |
      | null                    |

  Scenario Outline: Registro fallido por presión arterial inválida (sistólica/diastólica)
    When intento registrar el ingreso del paciente con los siguientes datos:
      | informe     | temperatura | frecuencia cardiaca | frecuencia respiratoria | frecuencia sistolica          | frecuencia diastolica          | nivel  |
      | Mareo leve  | 37.0        | 88                  | 17                      | <frecuencia sistolica>        | <frecuencia diastolica>        | 2      |
    Then el sistema muestra un mensaje de error "La presión arterial debe tener valores numéricos válidos para sistólica y diastólica"

    Examples:
      | frecuencia sistolica | frecuencia diastolica |
      | -120                 | 80                    |
      | texto                | 80                    |
      | null                 | 80                    |
      | 120                  | -80                   |
      | 120                  | texto                 |
      | 120                  | null                  |

  Scenario Outline: Registro fallido por prioridad inválida o nula
    When intento registrar el ingreso del paciente con los siguientes datos:
      | informe          | temperatura | frecuencia cardiaca | frecuencia respiratoria | frecuencia sistolica | frecuencia diastolica | nivel |
      | Dolor torácico   | 38.0        | 90                  | 19                      | 120                  | 80                    | <nivel> |
    Then el sistema muestra un mensaje de error "La prioridad ingresada no existe o es nula"

    Examples:
      | nivel |
      | null  |
      | 0     |
      | 6     |
      | texto |

  Scenario: Registro fallido porque el paciente no existe en el sistema
    Given que no existe en el sistema el paciente con dni 55555888
    When intento registrar el ingreso del paciente con los siguientes datos:
      | informe          | temperatura | frecuencia cardiaca | frecuencia respiratoria | frecuencia sistolica | frecuencia diastolica | nivel |
      | Dolor abdominal  | 37.5        | 80                  | 18                      | 120                  | 80                    | 3     |
    Then el sistema muestra un mensaje de error "El paciente no existe en el sistema y debe ser registrado antes del ingreso"

  Scenario Outline: Reordenamiento de la cola según el nivel de prioridad del nuevo ingreso
    Given que existen los siguientes ingresos en la cola de atención:
      | cuil          | nombre          | nivel | hora de ingreso |
      | 20-44555000-4 | Laura Medina    | 2     | 09:00           |
      | 20-44666000-4 | Pablo Fernández | 4     | 09:05           |
      | 20-44777000-4 | Luis Gómez      | 4     | 09:10           |
    When registro un nuevo ingreso para el paciente con los siguientes datos:
      | cuil          | nombre      | informe        | temperatura | frecuencia cardiaca | frecuencia respiratoria | frecuencia sistolica | frecuencia diastolica | nivel   | hora de ingreso |
      | 20-44477310-4 | Enzo Juarez | Dolor torácico | 38.5        | 95                  | 20                      | 130                  | 85                    | <nivel> | 09:15            |
    Then el nuevo ingreso se ubica en la posición <posicion> de la cola de atención

    Examples:
      | nivel | posicion |
      | 1     | 1        |
      | 3     | 2        |
      | 5     | 4        |

  Scenario Outline: Desempate en la cola de atención entre pacientes con el mismo nivel de prioridad
    Given que existen los siguientes ingresos en la cola de atención:
      | cuil           | nombre          | nivel | hora de ingreso |
      | 20-44555000-4 | Laura Medina    | 2     | 09:00           |
      | 20-44666000-4 | Pablo Fernández | 3     | 09:05           |
      | 20-44777000-4 | Luis Gómez      | 3     | 09:10           |
    When registro un nuevo ingreso para el paciente con los siguientes datos:
      | cuil          | nombre      | informe        | temperatura | frecuencia cardiaca | frecuencia respiratoria | frecuencia sistolica | frecuencia diastolica | nivel | hora de ingreso |
      | 20-44477310-4 | Enzo Juarez | Dolor torácico | 38.5        | 95                  | 20                      | 130                  | 85                    | 3     | <hora>          |
    Then el nuevo ingreso se ubica en la posición <posicion> de la cola de atención

    Examples:
      | hora   | posicion |
      | 09:04  | 2        |
      | 09:07  | 3        |
      | 09:12  | 4        |
