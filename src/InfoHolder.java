import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class InfoHolder {
    public final String name;
    public final int port;
    public final int lossPercentage;
    public final InetAddress parentIP;
    public final int parentPort;
    public Map <InetAddress, Integer> addressBook;
    public Map <String, Integer> unConfirmedMessages;
    public DatagramSocket socket;

    public InfoHolder (String name, int port, int lossPercentage) {
        this.lossPercentage = lossPercentage;
        this.name = name;
        this.parentIP = null;
        this.parentPort = -1;
        this.port = port;
        this.addressBook = new HashMap<>();
        this.unConfirmedMessages = new TreeMap<>();
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public InfoHolder (String name, int port, int lossPercentage, InetAddress parentIP, int parentPort) {
        this.lossPercentage = lossPercentage;
        this.name = name;
        this.parentIP = parentIP;
        this.parentPort = parentPort;
        this.port = port;
        this.addressBook = new HashMap<>();
        addressBook.put(parentIP, parentPort);
        this.unConfirmedMessages = new TreeMap<>();
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public boolean isIRoot() {
        if(parentIP == null) return true;
        return false;
    }

    public void putUncofirmedMessage (UUID datagramID) {
        int trial = 0;
        if (!unConfirmedMessages.containsKey(datagramID.toString())) {
            unConfirmedMessages.put(datagramID.toString(), 1);
        } else {
            trial = unConfirmedMessages.get(datagramID);
            unConfirmedMessages.remove(datagramID);
            unConfirmedMessages.put(datagramID.toString(), trial + 1);
        }
    }

    public InetAddress[] getAllAddresses(InetAddress srcOfMessage) {
        int i = 0;
        InetAddress[] inetAddresses = new InetAddress[addressBook.size()];
        for(Map.Entry<InetAddress, Integer> entry : addressBook.entrySet()) {
          //  if(!entry.getKey().equals(srcOfMessage.toString())) {
                inetAddresses[i++] = entry.getKey();
            //}
        }
        return inetAddresses;
    }

    public Integer[] getAllPorts(InetAddress[] addresses, InetAddress srcOfMessage) {
        int i = 0;
        Integer[] ports = new Integer[addressBook.size()];
        for(i = 0; i < addressBook.size(); i++) {
            ports[i] = addressBook.get(addresses[i]);
        }
        return ports;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.socket.close();
    }
}
