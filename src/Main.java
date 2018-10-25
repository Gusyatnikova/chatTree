import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class Main {
    // args/// name, port, lossPercentage, parentIP, parentPort)
    public static void main(String[] args) {
        try {
            InfoHolder infoHolder = null;
            if (args.length == 3) {
                infoHolder = new InfoHolder(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            } else if (args.length == 5) {
                infoHolder = new InfoHolder(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), InetAddress.getByName(args[3]), Integer.parseInt(args[4]));
            } else {
                System.out.println("Invalid input");
                System.exit(1);
            }

            Sender sender = new Sender(infoHolder);
            Receiver receiver = new Receiver(infoHolder, sender);
            Thread rt = new Thread(receiver);
            rt.start();
            System.out.println("receiver started " + InetAddress.getLocalHost().getHostAddress() + " " + Integer.parseInt(args[1]));
            Thread st = new Thread(sender);
            st.start();
            System.out.println("sender started");



        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}

