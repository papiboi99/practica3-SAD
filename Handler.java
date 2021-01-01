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
    static ExecutorService pool = Executors.newFixedThreadPool(2);
    static final int PROCESSING = 2;

    static Map<String, SocketChannel> clients = new TreeMap<>();

    final SocketChannel socketChannel;
    final SelectionKey selectionKey;
    ByteBuffer input = ByteBuffer.allocate(1024);
    static final int READING = 0, SENDING = 1;
    int state = READING;
    String clientName = "";
    String clientMessage;
    boolean isFirstTime = true;
    final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

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
                sendBroadcast();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void read() throws IOException {
        int readCount = socketChannel.read(input);

        //check whether the result is equal to -1, and close the connection if it is
        if(readCount == -1){
            this.socketChannel.close();
            clients.remove(clientName);
            System.out.println(dateFormat.format(new Date())+" [SERVER] " + clientName + " has just left");
            return;
        }
        if (readCount > 0) {
            state = PROCESSING;
            pool.execute(new Processer(readCount));
        }
        //We are interested in writing back to the client soon after read processing is done.
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

    /**
     * Processing of the read message. This only prints the message to stdOut.
     *
     * @param readCount
     */
    synchronized void readProcess(int readCount) {
        StringBuilder sb = new StringBuilder();
        input.flip();
        byte[] subStringBytes = new byte[readCount];
        byte[] array = input.array();
        System.arraycopy(array, 0, subStringBytes, 0, readCount);
        // Assuming ASCII (bad assumption but simplifies the example)
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
            isFirstTime = false;
        }else{
            clientMessage = sb.toString().trim();
            System.out.println(dateFormat.format(new Date())+" [SERVER] " + clientName + " says: \""+clientMessage+"\"");
        }
    }

    void sendBroadcast() throws IOException {
        ByteBuffer output;
        for(Map.Entry<String,SocketChannel> entry : clients.entrySet()) {
            if (entry.getKey() != clientName && clientMessage != null){
                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                output = ByteBuffer.wrap((dateFormat.format(new Date())+" >> ["+clientName+"]: "+clientMessage+"\n").getBytes());
                entry.getValue().write(output);
            }
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