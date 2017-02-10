import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Kevin McLaughlin on 1/21/2017.
 */
public class TCPConnectionClient implements ConnectionClient {

    private String hostName;
    private int portNumber;
    private Socket socket;

    public TCPConnectionClient(String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    public void connect() {
        try {
            socket = new Socket(InetAddress.getByName(hostName), portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(byte[] b) {
        try {
            socket.getOutputStream().write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] receiveMessage(int size) {
        byte[] message = new byte[size];
        try {
            socket.getInputStream().read(message, 0, message.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    public byte[] receiveMessage(int size, int timeout) {
        return new byte[0];
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
