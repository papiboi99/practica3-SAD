import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 6666;

    private static String nick;

    public static void main (String [] args){
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("[CLIENT] Please introduce your nick");
        System.out.print("> ");
        try {
            nick = input.readLine();
            System.out.println("[CLIENT] Hello "+nick+"! Now you are talking with everyone :)\n" +
                               "↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓");
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            System.out.println("Connected at "+dateFormat.format(new Date()));

        } catch (IOException e) {
            System.out.println("[CLIENT ERROR] Couldn't read from Keyboard");
        }

        MySocket s = new MySocket(nick, HOST, PORT);

        Thread inputThread = new Thread(() -> {
            try{
                String line;
                while ((line = input.readLine()) != null){
                    s.writeString(line);
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    System.out.println(dateFormat.format(new Date())+" << [ME]: "+line);
                }
            }catch (IOException e){
                System.out.println("[CLIENT ERROR] Couldn't read from Keyboard");
            }
        });
        inputThread.start();

        Thread outputThread = new Thread(() -> {
            String line;
            while ((line = s.readString()) != null){
                System.out.println(line);
            }
        });
        outputThread.start();
    }
}
