Feature: Registro de admisiones en urgencias
  Con el fin de que los pacientes sean atendidos cuanto antes por el médico según su nivel de prioridad
  Como enfermera
  Quiero poder registrar las admisiones de los pacientes a urgencias
#10 escenarios
  Background:
    Given la siguiente enfermera está autenticada en el sistema
      |  cuil         | nombre | apellido  | matricula | email           |
      | 20-30574930-4 | Maria  | Del Valle | ABC123    | maria@gmail.com |
    And existe en el sistema el paciente:
      | cuil           | nombre |
      | 20-44477310-4  | Enzo   |

  Scenario: Registro exitoso de admisión de un paciente existente
    When registro el ingreso del paciente con los siguientes datos:
      | informe          | temperatura | frecuencia cardiaca | frecuencia respiratoria | frecuencia sistolica | frecuencia diastolica | nivel     |
      | Dolor torácico   | 38.2        | 92                  | 19                      | 120                  | 80                    | 5         |
    Then el ingreso queda registrado en el sistema
    And el estado inicial del ingreso es "PENDIENTE"
    And el paciente entra en la cola de atención


  Rule: Escenarios negativos (campos invalidos)

  Scenario Outline: Registro fallido porque el informe es nulo o está vacío
    When registro el ingreso del paciente con los siguientes datos:
      | informe   | temperatura | frecuencia cardiaca | frecuencia respiratoria | frecuencia sistolica | frecuencia diastolica | nivel     |
      | <informe> | 37.5        | 80                  | 18                      | 120                  | 80                    | 3         |
    Then el sistema muestra un mensaje de error "El campo 'informe' es inválido: no puede estar vacío ni contener solo espacios"

    Examples:
      | informe |
      | null    |
      | ""      |
      | "   "   |

  Scenario Outline: Registro fallido por temperatura inválida
    When registro el ingreso del paciente con los siguientes datos:
      | informe         | temperatura   | frecuencia cardiaca | frecuencia respiratoria | frecuencia sistolica | frecuencia diastolica | nivel     |
      | Dolor abdominal | <temperatura> | 80                  | 18                      | 120                  | 80                    | 3         |
    Then el sistema muestra un mensaje de error "El campo 'temperatura' es inválido: debe tener valores positivos válidos (grados Celsius)"

    Examples:
      | temperatura |
      | -36.5       |
      | texto       |
      | null        |

  Scenario Outline: Registro fallido por frecuencia cardíaca inválida
    When registro el ingreso del paciente con los siguientes datos:
      | informe        | temperatura | frecuencia cardiaca       | frecuencia respiratoria | frecuencia sistolica | frecuencia diastolica | nivel |
      | Dolor torácico | 37.8        | <frecuencia cardiaca>     | 16                      | 120                  | 80                    | 5     |
    Then el sistema muestra un mensaje de error "El campo 'frecuenciaCardiaca' es inválido: debe tener valores positivos válidos (latidos por minuto)"

    Examples:
      | frecuencia cardiaca |
      | -75                 |
      | texto               |
      | null                |

  Scenario Outline: Registro fallido por frecuencia respiratoria inválida
    When registro el ingreso del paciente con los siguientes datos:
      | informe         | temperatura | frecuencia cardiaca | frecuencia respiratoria         | frecuencia sistolica | frecuencia diastolica | nivel   |
      | Cefalea intensa | 38.2        | 85                  | <frecuencia respiratoria>       | 125                  | 80                    | 4       |
    Then el sistema muestra un mensaje de error "El campo 'frecuenciaRespiratoria' es inválido: debe tener valores positivos válidos (respiraciones por minuto)"

    Examples:
      | frecuencia respiratoria |
      | -20                     |
      | texto                   |
      | null                    |

  Scenario Outline: Registro fallido por tensión arterial inválida (sistólica/diastólica)
    When registro el ingreso del paciente con los siguientes datos:
      | informe     | temperatura | frecuencia cardiaca | frecuencia respiratoria | frecuencia sistolica          | frecuencia diastolica          | nivel  |
      | Mareo leve  | 37.0        | 88                  | 17                      | <frecuencia sistolica>        | <frecuencia diastolica>        | 2      |
    Then el sistema muestra un mensaje de error "El campo 'tensionArterial' es inválido: debe tener valores positivos válidos para las frecuencias sistólica y diastólica (milimetros de mercurio)"

    Examples:
      | frecuencia sistolica | frecuencia diastolica |
      | -120                 | 80                    |
      | texto                | 80                    |
      | null                 | 80                    |
      | 120                  | -80                   |
      | 120                  | texto                 |
      | 120                  | null                  |

  Scenario Outline: Registro fallido por prioridad inválida o nula
    When registro el ingreso del paciente con los siguientes datos:
      | informe          | temperatura | frecuencia cardiaca | frecuencia respiratoria | frecuencia sistolica | frecuencia diastolica | nivel |
      | Dolor torácico   | 38.0        | 90                  | 19                      | 120                  | 80                    | <nivel> |
    Then el sistema muestra un mensaje de error "El campo 'nivel' es inválido: la prioridad ingresada no existe o es nula"

    Examples:
      | nivel |
      | null  |
      | 0     |
      | 6     |
      | texto |

  Scenario: Registro fallido porque el paciente no existe en el sistema
    Given que no existe en el sistema el paciente con cuil "20-55555888-4"
    When registro el ingreso del paciente con los siguientes datos:
      | informe          | temperatura | frecuencia cardiaca | frecuencia respiratoria | frecuencia sistolica | frecuencia diastolica | nivel |
      | Dolor abdominal  | 37.5        | 80                  | 18                      | 120                  | 80                    | 3     |
    Then el sistema muestra un mensaje de error "No se encontró 'paciente' con CUIL: 20-55555888-4"

   Rule: Logica del ordenamiento de la cola

   Scenario Outline: Reordenamiento de la cola según el nivel de prioridad del nuevo ingreso
    Given que existen los siguientes ingresos en la cola de atención:
      | cuil          | nombre          | nivel | hora de ingreso |
      | 20-44555000-4 | Laura Medina    | 2     | 09:00           |
      | 20-44666000-4 | Pablo Fernández | 4     | 09:05           |
      | 20-44777000-4 | Luis Gómez      | 4     | 09:10           |
    When ingresa a la cola el paciente con los siguientes datos:
      | cuil          | nombre      | nivel   | hora de ingreso |
      | 20-44477310-4 | Enzo Juarez | <nivel> | 09:15           |
    Then el nuevo ingreso se ubica en la posición <posicion> de la cola de atención

    Examples:
      | nivel | posicion |
      | 1     | 1        |
      | 3     | 2        |
      | 5     | 4        |

  Scenario Outline: Desempate en la cola de atención entre pacientes con el mismo nivel de prioridad
    Given que existen los siguientes ingresos en la cola de atención:
      | cuil           | nombre          | nivel | hora de ingreso |
      | 20-44555000-4  | Laura Medina    | 2     | 09:00           |
      | 20-44666000-4  | Pablo Fernández | 3     | 09:05           |
      | 20-44777000-4  | Luis Gómez      | 3     | 09:10           |
    When ingresa a la cola el paciente con los siguientes datos:
      | cuil          | nombre      | informe        | nivel | hora de ingreso |
      | 20-44477310-4 | Enzo Juarez | Dolor torácico | 3     | <hora>          |
    Then el nuevo ingreso se ubica en la posición <posicion> de la cola de atención

    Examples:
      | hora   | posicion |
      | 09:04  | 2        |
      | 09:07  | 3        |
      | 09:12  | 4        |
