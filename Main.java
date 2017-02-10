import java.nio.ByteBuffer;

/**
 * Created by Kevin McLaughlin on 1/19/2017.
 */
public class Main {

    public static final String HOSTNAME = "attu2.cs.washington.edu";

    public static void main(String[] args) {

        byte[] outputA = stepA();
        int numA = ByteBuffer.wrap(outputA, 12, 4).getInt();
        int lenA = ByteBuffer.wrap(outputA, 16, 4).getInt();
        int udp_portA = ByteBuffer.wrap(outputA, 20, 4).getInt();
        int secretA = ByteBuffer.wrap(outputA, 24, 4).getInt();
        System.out.println("Secret A: " + secretA);

        byte[] outputB = stepB(numA, lenA, udp_portA, secretA);
        int tcp_portB = ByteBuffer.wrap(outputB, 12, 4).getInt();
        int secretB = ByteBuffer.wrap(outputB, 16, 4).getInt();
        System.out.println("Secret B: " + secretB);

        ConnectionClient client = new TCPConnectionClient(HOSTNAME, tcp_portB);
        byte[] outputC = stepC(client, secretB, tcp_portB);
        int num2C = ByteBuffer.wrap(outputC, 12, 4).getInt();
        int len2C = ByteBuffer.wrap(outputC, 16, 4).getInt();
        int secretC = ByteBuffer.wrap(outputC, 20, 4).getInt();
        char characterC = (char)outputC[24];
        System.out.println("Secret C: " + secretC);

        byte[] outputD = stepD(client, num2C, len2C, secretC, characterC);
        int secretD = ByteBuffer.wrap(outputD, 12, 4).getInt();
        System.out.println("Secret D: " + secretD);
    }

    public static byte[] stepC(ConnectionClient client, int secret, int tcp_port) {
        client.connect();
        byte[] response = client.receiveMessage(28);
        return response;
    }

    public static byte[] stepA() {
        String payload = "hello world\0";
        ByteBuffer byteBuffer = MessageBuilder.headerConstructor(payload.length(), 0, (short)1);
        MessageBuilder.payloadConstructor(byteBuffer, payload);
        UDPConnectionClient client = new UDPConnectionClient(HOSTNAME, 12235);
        client.connect();
        client.sendMessage(byteBuffer.array());
        byte[] response = client.receiveMessage(28);
        client.disconnect();
        return response;
    }


    public static byte[] stepD(ConnectionClient client, int num, int len, int secret, char c) {
        ByteBuffer byteBuffer = MessageBuilder.headerConstructor(len, secret, (short)1);
        char[] characters = new char[len];
        for (int i = 0; i < characters.length; i++) {
            characters[i] = c;
        }
        MessageBuilder.payloadConstructor(byteBuffer, new String(characters).getBytes());
        for (int i = 0; i < num; i++) {
            client.sendMessage(byteBuffer.array());
        }
        byte[] response = client.receiveMessage(16);
        client.disconnect();
        return response;
    }

    //TODO: I Think there is an error in here that causes it to only sometimes work properly
    public static byte[] stepB(int num, int len, int udp_port, int secret) {
        ByteBuffer byteBuffer = MessageBuilder.headerConstructor(len + 4, secret, (short)1);

        // Converter for translating integers to byte array that can be placed in the message
        ByteBuffer intConverter = ByteBuffer.allocate(4);
        byte[] payloadArray = new byte[MessageBuilder.getPayloadLength(len + 4)];
        MessageBuilder.payloadConstructor(byteBuffer, payloadArray);

        // Create client for connection
        UDPConnectionClient client = new UDPConnectionClient(HOSTNAME, udp_port);
        client.connect();

        byte[] currentMessage = byteBuffer.array();

        for(int i = 0; i < num; i++) {
            client.sendMessage(currentMessage);
            byte[] response = client.receiveMessage(16, 500);
            if (response == null) {
                i--;
            } else {
                // Update the payload to contain the next i in the sequence
                // At the end so modification only happens on successful reception of response
                intConverter.putInt(i + 1);
                byte[] newInteger = intConverter.array();
                for (int j = 0; j < newInteger.length; j++) {
                    currentMessage[12 + j] = newInteger[j];
                }
                intConverter.clear();
            }
        }
        byte[] finalOutputB = client.receiveMessage(20);
        client.disconnect();
        return finalOutputB;
    }

    private static void printByteArray(byte[] input) {
        for (int i = 0; i < input.length; i++) {
            System.out.print(input[i] + " ");
        }
        System.out.println();
    }

}
