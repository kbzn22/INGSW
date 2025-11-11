Feature: Búsqueda de pacientes
  Con el fin de que los pacientes sean atendidos cuanto antes por el médico según su nivel de prioridad
  Como enfermera
  Quiero poder buscar un paciente por su CUIL
#4 escenarios
  Background:
    Given la enfermera está autenticada en el sistema
    And los pacientes existen en el sistema:
      | cuil          | nombre |
      | 20-44477310-4 | enzo   |
      | 20-43650619-4 | paula  |
      | 20-12345678-4 | mirtha |

  Scenario: Búsqueda por CUIL de paciente existente
    When busco el paciente con cuil "20-44477310-4"
    Then el sistema me muestra el paciente:
      | cuil          | nombre |
      | 20-44477310-4 | enzo   |

  Scenario: Búsqueda por CUIL de otro paciente existente
    When busco el paciente con cuil "20-43650619-4"
    Then el sistema me muestra el paciente:
      | cuil          | nombre |
      | 20-43650619-4 | paula  |

  Scenario: Búsqueda por CUIL de paciente inexistente
    When busco el paciente con cuil "20-99999999-4"
    Then el sistema muestra mensaje de error "No se encontró un paciente con CUIL: 20-99999999-4"

  Scenario Outline: Búsqueda con CUIL inválido (sin formato de cuil)
    When busco el paciente con cuil "<cuil>"
    Then el sistema muestra mensaje de error "El CUIL ingresado es inválido: <cuil>"

    Examples:
    | cuil     |
    | 205454   |
    | 25-45444 |
    | ABC123   |