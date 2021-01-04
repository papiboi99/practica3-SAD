# practica3-SAD

### INFO

Esta práctica se ha realizado con un servidor tipo selector nio y
patrón reactor. Además se ha usado la variante con varios agentes, 
es decir, implementando una thread pool. Se puede aumentar el número,
de agentes para reducir tiempos pero también aumenta la carga
de procesado.

### EJECUCIÓN

Para probar la aplicación se tiene que ejecutar el servidor
y los clientes por separado. Los clientes se deben ejecutar en
modo paralelo en tu entorno de programación (puedes ejecutar
todos los que quieras a la vez).

Un ejemplo de ejecución sería:

1) Hacer un run de la clase Server
2) Hacer un run de la clase Client e introducir primer nombre
3) Hacer otro run de la clase Client e introducir segundo nombre
4) Escribir entre clientes

*El cliente que estás usando sale en el nombre de la ventana*

### MENÚS

#### LOGIN

- **Info Label:** Te indica que debes introducir tu nickname.
- **Nickname Label:** Te indica donde debes introducir el nickname.
- **Nickname Text Field:** Campo de texto para introducir tu nickname.
  Con un intro pasas al siguiente menú de chat (si no has introducido ningún carácter
  aparece un aviso informando al usuario del error cometido).
- **Join Chat Button:** Haciendo click pasas al siguiente menú de chat (si no has introducido ningún carácter
  aparece un aviso informando al usuario del error cometido).

#### CHAT

- **Chat Area:** Area no editable donde se printea todos los mensajes (enviados y recibidos).
  Contenido en un Scroll Pane.
- **Message Area:** Campo de texto para introducir el mensaje a enviar. Contenido en un Scroll Pane.
- **Send Button:** Haciendo click envías el mensaje (si no has introducido ningún carácter
  aparece un aviso informando al usuario del error cometido).
- **Users List:** Lista de usuarios conectados en ese momento. Se va actualizando automáticamente,
  tanto para usuarios que se conectan como para los que se desconectan.
  Contenido en un Scroll Pane.
- **Disconnect Button:** Haciendo click te desconectas del chat y cierras la aplicación.


_Formato de mensaje al unirse al chat:_

    [CLIENT] Hello tu_nickname! Now you are talking with everyone :)
    ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓
    Connected at dd-MM-yyyy HH:mm:ss

_Formato de mensaje enviado:_

    HH:mm << [ME]: "mensaje_enviado"

_Formato de mensaje recibido:_

    HH:mm >> [nickname_cliente]: "mensaje_recibido"




