package src.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Handler implements Runnable {
    static final int READING = 0, SENDING = 1;
    static final int CLIENT_CONNECTED = 0, CLIENT_DISCONNECTED = 1, CLIENT_MESSAGE = 2;
    static final int PROCESSING = 2;

    static ExecutorService pool = Executors.newFixedThreadPool(2);
    static Map<String, SocketChannel> clients = new TreeMap<>();

    final SocketChannel socketChannel;
    final SelectionKey selectionKey;
    final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    ByteBuffer input = ByteBuffer.allocate(1024);
    int state = READING;
    boolean isFirstTime = true;
    String clientName = "";
    String clientMessage;

    Handler(Selector selector, SocketChannel c) throws IOException {
        socketChannel = c;
        c.configureBlocking(false);
        selectionKey = socketChannel.register(selector, 0);
        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    @Override
    public void run() {
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                sendManager(CLIENT_MESSAGE);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void read() throws IOException {
        int readCount = socketChannel.read(input);
        if (readCount > 0) {
            state = PROCESSING;
            pool.execute(new Processer(readCount));
        }
        selectionKey.interestOps(SelectionKey.OP_WRITE);
    }

    //Start processing in a new Processer Thread and Hand off to the reactor thread.
    synchronized void processAndHandOff(int readCount) {
        readProcess(readCount);
        //Read processing done. Now the server is ready to send a message to the client.
        state = SENDING;
    }

    class Processer implements Runnable {
        int readCount;
        Processer(int readCount) {
            this.readCount =  readCount;
        }
        public void run() {
            processAndHandOff(readCount);
        }
    }

    synchronized void readProcess(int readCount) {
        StringBuilder sb = new StringBuilder();
        input.flip();
        byte[] subStringBytes = new byte[readCount];
        byte[] array = input.array();
        System.arraycopy(array, 0, subStringBytes, 0, readCount);
        // Assuming ASCII (bad assumption but works for this case)
        sb.append(new String(subStringBytes));
        input.clear();

        if (isFirstTime){
            clientName = sb.toString().trim();
            int i = isNickUsed(clientName);
            if (i != 0){
                clientName = clientName+i;
            }
            clients.put(clientName, socketChannel);
            System.out.println(dateFormat.format(new Date())+" [SERVER] " + clientName + " has just connected!");
            try {
                sendManager(CLIENT_CONNECTED);

            } catch (IOException e) {
                //e.printStackTrace();
            }
            isFirstTime = false;
        }else{
            clientMessage = sb.toString().trim();
            if(clientMessage.equals("EXIT")){
                try {
                    sendManager(CLIENT_DISCONNECTED);
                    System.out.println(dateFormat.format(new Date())+" [SERVER] " + clientName + " has just left");
                    socketChannel.close();
                    clients.remove(clientName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }else{
                System.out.println(dateFormat.format(new Date())+" [SERVER] " + clientName + " says: \""+clientMessage+"\"");
            }
        }
    }

    void send(String text, SocketChannel s) throws IOException{
        ByteBuffer output = ByteBuffer.wrap((text).getBytes());
        s.write(output);
    }

    void sendManager(int type) throws IOException {
        String toOtherClients;
        switch (type) {
            case CLIENT_CONNECTED:
                toOtherClients = "[CLIENT CONNECTED] " + clientName + "\n";

                String toMyClient = "[HELLO CLIENT] Connected at " + dateFormat.format(new Date()) +
                        "\n[CLIENT LIST]";
                for(Map.Entry<String,SocketChannel> entry : clients.entrySet()) {
                    toMyClient = toMyClient + ";" + entry.getKey();
                }
                send(toMyClient+"\n", socketChannel);

                for(Map.Entry<String,SocketChannel> entry : clients.entrySet()) {
                    if (entry.getKey() != clientName){
                        send(toOtherClients,entry.getValue());
                    }
                }
                break;

            case CLIENT_DISCONNECTED:
                toOtherClients = "[CLIENT DISCONNECTED] " + clientName + "\n";
                for(Map.Entry<String,SocketChannel> entry : clients.entrySet()) {
                    if (entry.getKey() != clientName && clientMessage != null){
                        send(toOtherClients,entry.getValue());
                    }
                }
                break;

            case CLIENT_MESSAGE:
                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                toOtherClients = dateFormat.format(new Date()) +" >> ["+clientName+"]: " + clientMessage + "\n";
                for(Map.Entry<String,SocketChannel> entry : clients.entrySet()) {
                    if (entry.getKey() != clientName && clientMessage != null){
                        send(toOtherClients,entry.getValue());
                    }
                }
                break;
        }
        selectionKey.interestOps(SelectionKey.OP_READ);
        state = READING;
    }

    int isNickUsed(String myNick){
        int i = 0;
        for(Map.Entry<String,SocketChannel> entry : clients.entrySet()) {
            if (entry.getKey().equals(myNick)){
                i++;
            }
        }
        return i;
    }
}