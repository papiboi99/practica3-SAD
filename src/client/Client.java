package src.client;

import src.swing.ChatFrame;
import javax.swing.*;
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
        // When CR hit
        chatFrame.getLoginPanel().getNicknameField().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                chatFrame.getLoginPanel().getJoinButton().doClick();
            }
        });

        // Join button clicked
        chatFrame.getLoginPanel().getJoinButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                nickname = chatFrame.getLoginPanel().getNicknameField().getText();
                if(nickname.isEmpty()){
                    JOptionPane.showMessageDialog(null,
                            "Could not join the chat, please introduce a nickname",
                            "Empty Nickname",
                            JOptionPane.ERROR_MESSAGE);
                }else{
                    connectToServer();
                }
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
                String message = chatFrame.getChatPanel().getMessageField().getText();
                if(message.isEmpty()){
                    JOptionPane.showMessageDialog(null,
                            "Could not send the message, please write at least one character",
                            "Empty Message",
                            JOptionPane.ERROR_MESSAGE);
                }else{
                    sendMessage(message);
                    chatFrame.getChatPanel().getMessageField().setText("");
                }
            }
        });

        // Disconnect button clicked
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
