Feature: Búsqueda de pacientes
  Con el fin de que los pacientes sean atendidos cuanto antes por el médico según su nivel de prioridad
  Como enfermera
  Quiero poder buscar un paciente por su DNI
#4 escenarios
  Background:
    Given la enfermera está autenticada en el sistema
    And los pacientes existen en el sistema:
      | dni      | nombre        |
      | 44477310 | enzo juarez   |
      | 43650619 | paula madrid  |
      | 12345678 | mirtha legrand|

  Scenario: Búsqueda por DNI de paciente existente
    When busco el paciente con dni "44477310"
    Then el sistema me muestra el paciente:
      | dni      | nombre      |
      | 44477310 | enzo juarez |

  Scenario: Búsqueda por DNI de otro paciente existente
    When busco el paciente con dni "43650619"
    Then el sistema me muestra el paciente:
      | dni      | nombre      |
      | 43650619 | paula madrid |

  Scenario: Búsqueda por DNI de paciente inexistente
    When busco el paciente con dni "99999999"
    Then el sistema muestra un mensaje de error "Paciente no encontrado"

  Scenario: Búsqueda con DNI inválido (alfanumérico)
    When busco el paciente con dni "ABC123"
    Then el sistema muestra un mensaje de error "DNI inválido"