Feature: Registro de admisiones con prioridad
  Con el fin de determinar que pacientes tienen mayor prioridad de atencion
  Como enfermera
  Quiero poder registrar las admisiones de los pacientes a urgencias

  Background: la enfermera esta autenticada en el sistema

  Scenario: Registro de un paciente existente en el sistema
    Given que existe el paciente "Mateo Ruiz" en el sistema
    When se registra su ingreso junto con los datos:
    |informe | nivel | temperatura | frecuencia cardiaca | frecuencia respiratoria | tension arterial|
    | Dolor abdominal  | Rojo   | 37.8        | 85                  | 18                      | 120/80           |
    Then el ingreso queda registrado en el sistema
    And el paciente entra en la cola de atención
    And el estado inicial del ingreso es "PENDIENTE"




