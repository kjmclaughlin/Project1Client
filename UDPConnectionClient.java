import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.*;

/**
 * Created by Kevin McLaughlin on 1/19/2017.
 */
public class UDPConnectionClient implements ConnectionClient {

    private String hostName;
    private int portNumber;
    private DatagramSocket socket;

    public UDPConnectionClient(String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    public void connect() {
        try {
            socket = new DatagramSocket();
            socket.connect(InetAddress.getByName(hostName), portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(byte[] message) {
        DatagramPacket packet = null;
        try {
            packet = new DatagramPacket(message, message.length, InetAddress.getByName(hostName), portNumber);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] receiveMessage(int messageSize) {
        byte[] response = new byte[messageSize];
        DatagramPacket responsePacket = new DatagramPacket(response, response.length);
        try {
            socket.receive(responsePacket);
        } catch (SocketTimeoutException e) {
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responsePacket.getData();
    }

    public byte[] receiveMessage(int messageSize, int timeout) {
        try {
            socket.setSoTimeout(timeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return receiveMessage(messageSize);
    }

    public void disconnect() {
        socket.close();
    }

}
