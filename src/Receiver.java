import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Receiver implements Runnable {

    private InfoHolder infoHolder;
    private Sender sender;

    public Receiver(InfoHolder infoHolder, Sender sender) {
        this.infoHolder = infoHolder;
        this.sender = sender;
    }

    public void run() {
        try {
            byte[] buff = new byte[200];
            while(true) {
                DatagramPacket receivedPacket = new DatagramPacket(buff, buff.length);
                infoHolder.socket.receive(receivedPacket);
                System.out.println(infoHolder.name + " get message" + "from" + receivedPacket.getAddress() + " " + infoHolder.port);
                analyzeMessage(receivedPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void analyzeMessage (DatagramPacket packet) {
        char rcvCode = getCode(packet);
        String message;
        if(rcvCode == 0) {
            //add child
            if(!infoHolder.addressBook.containsKey(packet.getAddress())) {
                System.out.println("i register new child with address/port : " + packet.getAddress() + " / " + packet.getPort());
                infoHolder.addressBook.put(packet.getAddress(), packet.getPort());
                sender.sendMessage(packet.getAddress(), packet.getPort(), (char) 0, null);
            } else if (rcvCode == 1){
                //we get confirmation
                infoHolder.unConfirmedMessages.remove(getUUID(packet));
            } else if (rcvCode == 2) {
                message = getMessage(packet);
                System.out.println(infoHolder.name + " get message " + message);
            }
        }
    }

    char getCode(DatagramPacket packet) {
        char code = 0;
        String[] parts = new String[3];
        String dest = new String(packet.getData());
        parts = dest.split("¬");
        code = parts[1].toCharArray()[1];
        return code;
    }

    String getUUID(DatagramPacket packet) {
        String[] parts = new String[3];
        String dest = new String(packet.getData());
        parts = dest.split("¬");
        return parts[0];
    }

    String getMessage(DatagramPacket packet) {
        String[] parts = new String[3];
        String dest = new String(packet.getData());
        parts = dest.split("¬");
        return parts[2];
    }
}