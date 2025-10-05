Feature: Modulo de Urgencias
  Esta feature esta relacionada al registro de ingresos de pacientes en la sala de urgencias
  respetando su nivel de prioridad y el horario de llegada.

  Background:
    Given que la siguiente enfermera esta registrada:
    |Nombre|Apellido|
    |Maria |Perez   |
    # 1)
    Scenario: Ingreso del primer paciente a lista de espera de urgencias
      Given Dado que estan registrados los siguientes pacientes:
      |Cuil         |Apellido|Nombre   |Obra Social  |
      |20-44866040-1|Boggio  |Eliseo   |Boreal       |
      |27-4567890-3 |Dufour  |Alexandra|Swiss Medical|
      When Ingresa a urgencias el siguiente paciente:
      |Cuil         |informe         |Nivel de Emergencia|Temperatura|Frecuencia Cardiaca|Frecuencia Respiratoria|Tension Arterial|
      |20-44866040-1|Le agarro dengue|Emergencia         |38         |70                 |15                     |120/80          |
      Then La lista de espera esta ordenada por el cuil de la siguiente manera:
      |20-44866040-1|
      And El ingreso del paciente "20-44866040-1" esta en estado "PENDIENTE"
      And El ingreso del paciente "20-44866040-1" tiene enfermera "Maria Perez"

    # 2) Paciente NO EXISTE -> debe crearse antes de registrar el ingreso
  Scenario: Paciente inexistente se crea y luego se registra el ingreso
    Given No existe aun el paciente con cuil "23-22222222-2"
    When Ingresa a urgencias el siguiente paciente:
      | Cuil          | Informe          | Nivel de Emergencia | Temperatura | Frecuencia Cardiaca | Frecuencia Respiratoria | Tension Arterial |
      | 23-22222222-2 | Cefalea intensa | Urgencia Menor      | 36.8        | 65                  | 14                       | 110/70           |
    Then El paciente con cuil "23-22222222-2" fue creado
    And El ingreso del paciente "23-22222222-2" esta en estado "PENDIENTE"

  # 3) Mandatorios omitidos -> error indicando cual falta
  Scenario Outline: Faltan datos mandatorios al registrar ingreso
    Given Dado que estan registrados los siguientes pacientes:
      | Cuil          | Apellido | Nombre | Obra Social |
      | 24-33333333-3 | Diaz     | Luis   | OSDE        |
    When Intento ingresar a urgencias el siguiente paciente (capturando errores):
      | Cuil          | Informe   | Nivel de Emergencia | Temperatura | Frecuencia Cardiaca | Frecuencia Respiratoria | Tension Arterial |
      | 24-33333333-3 | <Informe> | <Nivel>             | <Temp>      | <FC>                | <FR>                     | <TA>             |
    Then Veo error con mensaje "<Mensaje>"

    Examples:
      | Informe | Nivel          | Temp | FC  | FR  | TA     | Mensaje                                  |
      |         | Urgencia       | 37   | 70  | 16  | 120/80 | El campo 'informe' es obligatorio        |
      | Dolor   |                | 37   | 70  | 16  | 120/80 | El campo 'nivelEmergencia' es obligatorio|
      | Dolor   | Urgencia       | 37   |     | 16  | 120/80 | El campo 'frecuenciaCardiaca' es obligatorio |
      | Dolor   | Urgencia       | 37   | 70  |     | 120/80 | El campo 'frecuenciaRespiratoria' es obligatorio |
      | Dolor   | Urgencia       | 37   | 70  | 16  |        | El campo 'tensionArterial' es obligatorio |

    # 4) FC o FR negativos -> error
  Scenario Outline: Frecuencias negativas no permitidas
    Given Dado que estan registrados los siguientes pacientes:
      | Cuil          | Apellido | Nombre | Obra Social |
      | 25-44444444-4 | Gomez    | Sofia  | Boreal      |
    When Intento ingresar a urgencias el siguiente paciente (capturando errores):
      | Cuil          | Informe  | Nivel de Emergencia | Temperatura | Frecuencia Cardiaca | Frecuencia Respiratoria | Tension Arterial |
      | 25-44444444-4 | Mareo    | Urgencia            | 36.5        | <FC>                | <FR>                     | 110/70           |
    Then Veo error con mensaje "<Mensaje>"

    Examples:
      | FC  | FR | Mensaje                                            |
      | -10 | 18 | La frecuencia cardiaca no puede ser negativa       |
      | 72  | -5 | La frecuencia respiratoria no puede ser negativa   |

    # 5) Prioridad: X > Y -> A antes que B
  Scenario: Un paciente con mayor nivel de emergencia desplaza a uno de menor nivel
    Given Dado que estan registrados los siguientes pacientes:
      | Cuil          | Apellido | Nombre | Obra Social |
      | 26-55555555-5 | Ruiz     | Pedro  | IOMA        |
      | 27-66666666-6 | Sosa     | Carla  | PAMI        |
    And La lista de espera contiene los siguientes ingresos (en orden de llegada):
      | Cuil          | Nivel de Emergencia |
      | 27-66666666-6 | Urgencia            |
    When Ingresa a urgencias el siguiente paciente:
      | Cuil          | Informe        | Nivel de Emergencia | Temperatura | Frecuencia Cardiaca | Frecuencia Respiratoria | Tension Arterial |
      | 26-55555555-5 | Dolor torácico | Emergencia          | 37.0        | 80                  | 20                       | 130/90           |
    Then La lista de espera esta ordenada por el cuil de la siguiente manera:
      | 26-55555555-5 |
      | 27-66666666-6 |

    # 6) Prioridad: X < Y -> B antes que A
  Scenario: Un paciente con menor nivel de emergencia queda despues del que ya estaba con mayor nivel
    Given Dado que estan registrados los siguientes pacientes:
      | Cuil          | Apellido | Nombre | Obra Social |
      | 28-77777777-7 | Paz      | Bruno  | OSDE        |
      | 29-88888888-8 | Vera     | Julia  | Swiss Med   |
    And La lista de espera contiene los siguientes ingresos (en orden de llegada):
      | Cuil          | Nivel de Emergencia |
      | 29-88888888-8 | Emergencia          |
    When Ingresa a urgencias el siguiente paciente:
      | Cuil          | Informe | Nivel de Emergencia | Temperatura | Frecuencia Cardiaca | Frecuencia Respiratoria | Tension Arterial |
      | 28-77777777-7 | Tos     | Urgencia Menor      | 37.3        | 78                  | 17                       | 115/75           |
    Then La lista de espera esta ordenada por el cuil de la siguiente manera:
      | 29-88888888-8 |
      | 28-77777777-7 |

     # 7) Prioridad: X = Y -> el que llego antes va primero (B ya estaba)
  Scenario: Dos pacientes con mismo nivel, se atiende primero el que ya esta esperando
    Given Dado que estan registrados los siguientes pacientes:
      | Cuil          | Apellido | Nombre | Obra Social |
      | 30-99999999-9 | Leon     | Maia   | IOMA        |
      | 31-00000000-0 | Rey      | Tomas  | PAMI        |
    And La lista de espera contiene los siguientes ingresos (en orden de llegada):
      | Cuil          | Nivel de Emergencia |
      | 31-00000000-0 | Urgencia            |
    When Ingresa a urgencias el siguiente paciente:
      | Cuil          | Informe   | Nivel de Emergencia | Temperatura | Frecuencia Cardiaca | Frecuencia Respiratoria | Tension Arterial |
      | 30-99999999-9 | Fiebre    | Urgencia            | 38.2        | 82                  | 22                       | 120/80           |
    Then La lista de espera esta ordenada por el cuil de la siguiente manera:
      | 31-00000000-0 |
      | 30-99999999-9 |