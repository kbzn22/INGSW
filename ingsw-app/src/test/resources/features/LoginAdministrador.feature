Feature Login de administador
  Con el fin de realizar un ingreso a la guardia
  Como administador
  Quiero ingresar al sistema con mi usuario y contrasena

  Background:
    Given los siguientes usuarios existen en el sistema
      |  cuil         | nombre | apellido  | Matricula |
      | 20-30574930-4 | Maria  | Del Valle | ABC123    |
      | 20-12547856-4 | Enzo   | Juarez    | ABC124    |
    And tienen los siguientes usuarios creados
      |  Usuario      | Contrasenia |
      | delvallem     | contr123    |
      | juareze       | contr456    |


    Scenario:Iniciar de sesion
      When ingreso el usuario "delvallem" y la contraseña "contr123"
      Then el sistema autentica el usuario con cuil "20-30574930-4"

    Scenario: Iniciar sesion con contraseña incorrecta
      When ingreso el usuario "delvallem" y la contraseña "contr158"
      Then el sistema informa que la contraseña es incorrecta

    Scenario: Iniciar sesion con usuario inexistente
      When ingreso el usuario "lizarragaj" y la contraseña "contr123"
      Then el sistema informa que el usuario no existe

