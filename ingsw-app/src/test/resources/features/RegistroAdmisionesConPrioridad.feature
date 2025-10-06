  Feature: Registro de admisiones en urgencias
  Con el fin de que los pacientes sean atendidos cuanto antes por el médico según su nivel de prioridad
  Como enfermera
  Quiero poder registrar las admisiones de los pacientes a urgencias
#7 escenarios, quedan mas por definir
  Background:
    Given la enfermera está autenticada en el sistema
    And existe en el sistema el paciente:
      | dni      | nombre      |
      | 44477310 | enzo juarez |
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
      | Dolor torácico   | 38.2        | 92                  | 19                      | 120                  |80                     | 5         |
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
    Then el sistema muestra un mensaje de error "La temperatura debe ser un número positivo en grados Celsius"

    Examples:
      | temperatura |
      | -36.5       |
      | texto       |
      | null        |
      | 0           |

  Scenario Outline: Registro fallido por frecuencia cardíaca inválida
    When intento registrar el ingreso del paciente con los siguientes datos:
      | informe        | temperatura | frecuencia cardiaca       | frecuencia respiratoria | frecuencia sistolica | frecuencia diastolica | nivel |
      | Dolor torácico | 37.8        | <frecuencia cardiaca>     | 16                      | 120                  | 80                    | 5     |
    Then el sistema muestra un mensaje de error "La frecuencia cardíaca debe ser un número positivo (latidos por minuto)"

    Examples:
      | frecuencia cardiaca |
      | -75                 |
      | texto               |
      | null                |
      | 0                   |

  Scenario Outline: Registro fallido por frecuencia respiratoria inválida
    When intento registrar el ingreso del paciente con los siguientes datos:
      | informe         | temperatura | frecuencia cardiaca | frecuencia respiratoria         | frecuencia sistolica | frecuencia diastolica | nivel   |
      | Cefalea intensa | 38.2        | 85                  | <frecuencia respiratoria>       | 125                  | 80                    | 4       |
    Then el sistema muestra un mensaje de error "La frecuencia respiratoria debe ser un número positivo (respiraciones por minuto)"

    Examples:
      | frecuencia respiratoria |
      | -20                     |
      | texto                   |
      | null                    |
      | 0                       |

  Scenario Outline: Registro fallido por presión arterial inválida (sistólica/diastólica)
    When intento registrar el ingreso del paciente con los siguientes datos:
      | informe     | temperatura | frecuencia cardiaca | frecuencia respiratoria | frecuencia sistolica          | frecuencia diastolica          | nivel  |
      | Mareo leve  | 37.0        | 88                  | 17                      | <frecuencia sistolica>        | <frecuencia diastolica>        | 2      |
    Then el sistema muestra un mensaje de error "La presión arterial debe tener valores numéricos positivos para sistólica y diastólica"

    Examples:
      | frecuencia sistolica | frecuencia diastolica |
      | -120                 | 80                    |
      | texto                | 80                    |
      | null                 | 80                    |
      | 0                    | 80                    |
      | 120                  | -80                   |
      | 120                  | texto                 |
      | 120                  | null                  |
      | 120                  | 0                     |

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

