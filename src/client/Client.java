package src.client;

import src.swing.ChatFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 6666;

    private static String nickname;
    private static ChatFrame chatFrame;
    private static MySocket s;

    public static void main (String [] args){
        chatFrame = new ChatFrame();
        setupNickname();
    }

    public static void setupNickname(){
        chatFrame.getLoginPanel().getNicknameField().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                chatFrame.getLoginPanel().getJoinButton().doClick();
            }
        });

        chatFrame.getLoginPanel().getJoinButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                nickname = chatFrame.getLoginPanel().getNicknameField().getText();
                connectToServer();
            }
        });
    }

    public static void setupChat(String connection){
        chatFrame.setupChatPanel(nickname);
        chatFrame.getChatPanel().getChatText().append(connection);

        // Send Button clicked
        chatFrame.getChatPanel().getSendButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                sendMessage(chatFrame.getChatPanel().getMessageField().getText());
                chatFrame.getChatPanel().getMessageField().setText("");
            }
        });

        // When hit enter
        chatFrame.getChatPanel().getMessageField().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                chatFrame.getChatPanel().getSendButton().doClick();
            }
        });

        chatFrame.getChatPanel().getDisconnectButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                disconnect();
            }
        });
    }

    public static void sendMessage(String message){
        s.writeString(message);
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        writeInChat(dateFormat.format(new Date())+" << [ME]: "+message);
    }

    public static void connectToServer(){
        s = new MySocket(nickname, HOST, PORT);

        // Inicializamos el thread que escucha respuestas del servidor
        Thread outputThread = new Thread(() -> {
            String line;
            while ((line = s.readString()) != null){
                if (line.contains("[HELLO CLIENT]")){
                    String str = line.substring(line.indexOf("]") + 2);
                    setupChat(str);

                    line = s.readString();
                    str = line.substring(line.indexOf("]") + 1);
                    while(str.contains(";")){
                        String user;
                        if (str.indexOf(";", 1) != -1){
                            user = str.substring(1, str.indexOf(";", 1));
                            str = str.substring(str.indexOf(";", 1));
                        }else{
                            user = str.substring(1);
                            str = "";
                        }
                        chatFrame.getChatPanel().getUsersList().addElement(user);
                        chatFrame.revalidate();
                        chatFrame.repaint();
                    }

                }else if (line.contains("[CLIENT CONNECTED]")){
                    String str = line.substring(line.indexOf("]") + 2);
                    chatFrame.getChatPanel().getUsersList().addElement(str);

                }else if (line.contains("[CLIENT DISCONNECTED]")) {
                    String str = line.substring(line.indexOf("]") + 2);
                    chatFrame.getChatPanel().getUsersList().removeElement(str);

                }else {
                    writeInChat(line);
                }
            }
        });
        outputThread.start();
    }

    public static void writeInChat(String text){
        chatFrame.getChatPanel().getChatText().append("\n" + text);
    }

    public static void disconnect(){
        s.close();
        chatFrame.dispose();
        System.exit(0);
    }
}
