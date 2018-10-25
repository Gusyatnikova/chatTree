import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.UUID;

public class Sender implements Runnable {

    private InfoHolder infoHolder;

    public Sender(InfoHolder infoHolder) {
        this.infoHolder = infoHolder;

    }

    public void run() {
        boolean connected = false;
        this.sendWelcomePack();
        Scanner scanner = new Scanner(System.in);
        String message;
        while (true) {
            System.out.println("Please, print your message! ");
            message = scanner.nextLine();
            try {
                sendMessageGlobal(message,InetAddress.getByName(InetAddress.getLocalHost().getHostAddress()));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendWelcomePack() {
        if (!infoHolder.isIRoot()) {
            try {
                byte[] sendBuf = new byte[200];
                UUID uuid = UUID.randomUUID();
                sendBuf = new String(uuid.toString() + "¬" + "1").getBytes();
                DatagramPacket welcomPack = new DatagramPacket(sendBuf, sendBuf.length, infoHolder.parentIP, infoHolder.parentPort);
                infoHolder.socket.send(welcomPack);
                infoHolder.putUncofirmedMessage(uuid);
                System.out.println("from " + infoHolder.name + " was sent welcome packet");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(InetAddress destAddr, int destPort, char code, String message) {
        UUID messageID = UUID.randomUUID();
        String fullMessage = generateMessage(messageID, code, message);
        byte[] buf = new byte[300];
        buf = fullMessage.getBytes();
        try {
            DatagramPacket outPack = new DatagramPacket(buf, buf.length, destAddr, destPort);
            infoHolder.socket.send(outPack);
            System.out.println(infoHolder.name + " sent message to " + destAddr + " " + destPort);
            infoHolder.putUncofirmedMessage(messageID);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generateMessage(UUID uuid, char code, String message) {
        String output = null;
        if (message == null) {
            output = uuid.toString() + "¬" + Character.toString(code);
        } else {
            output = uuid.toString() + "¬" + Character.toString(code) + "¬" + message;
        }
        return output;
    }

    public void sendMessageGlobal(String message, InetAddress source) {
        int count_receivers = infoHolder.addressBook.size();
        InetAddress[] addresses = new InetAddress[count_receivers];
        Integer[] ports = new Integer[count_receivers];

        addresses = infoHolder.getAllAddresses(source);
        ports = infoHolder.getAllPorts(addresses, source);
        for (int i = 0; i < count_receivers; i++) {
            sendMessage(addresses[i], ports[i], (char) 2, message);
        }
    }
}
