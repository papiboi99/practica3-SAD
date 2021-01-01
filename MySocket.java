import java.io.*;
import java.net.Socket;

public class MySocket {
    private Socket mySocket;
    private BufferedReader in;
    private PrintWriter out;
    private String nick;

    //construimos un socket para conectar con un servidor indicado
    public MySocket(String nick, String host, int port) {
        try {
            this.nick = nick;
            mySocket = new Socket(host, port);
            iniStreams();
            writeString(nick);

        } catch (IOException e) {
            System.out.println("[SOCKET ERROR] Couldn't create the socket");
            e.printStackTrace();
        }
    }

    //creamos un MySocket a partir de otro Socket
    public MySocket(Socket s) {
        mySocket = s;
        iniStreams();
    }

    private void iniStreams() {
        try {
            in = new BufferedReader(new InputStreamReader(
                    mySocket.getInputStream()
            ));
            out = new PrintWriter(mySocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("[SOCKET ERROR] Couldn't initialize the streams");
        }
    }

    //lee el siguiente String si lo hay, sino devuelve null
    public String readString() {
        try {
            return in.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    //lee el siguiente Int si lo hay, sino devuelve null
    public int readInt() {
        return Integer.parseInt(readString());
    }

    //lee el siguiente Boolean si lo hay, sino devuelve null
    public boolean readBoolean() {
        return Boolean.parseBoolean(readString());
    }

    //escribe el string en el socket
    public void writeString(String s) {
        out.println(s);
    }

    //escribe el int en el socket
    public void writeInt(int i) {
        writeString(Integer.toString(i));
    }

    //cerramos el socket
    public void close() {
        try {
            in.close();
            out.close();
            mySocket.close();
        } catch (IOException e) {
            System.out.println("[SOCKET ERROR] Couldn't close the socket");
        }
    }
}