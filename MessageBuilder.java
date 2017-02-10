import java.nio.ByteBuffer;

/**
 * Created by Kevin McLaughlin on 1/19/2017.
 */
public class MessageBuilder {

    private final static short STUDENT_NUMBER = 649;

    public static ByteBuffer headerConstructor(int payloadLen, int psecret, short step) {
        ByteBuffer b = ByteBuffer.allocate(12 + getPayloadLength(payloadLen)).putInt(payloadLen).putInt(psecret).putShort(step).putShort(STUDENT_NUMBER);
        return b;
    }

    public static void payloadConstructor(ByteBuffer buffer, String body) {
        payloadConstructor(buffer, body.getBytes());
    }

    public static void payloadConstructor(ByteBuffer buffer, byte[] bytes) {
        for (Byte b : bytes) {
            buffer.put(b);
        }
        if (bytes.length % 4 > 0) {
            for (int i = 0; i < 4 - (bytes.length % 4); i++) {
                buffer.put((byte) 0);
            }
        }
    }

    public static int getPayloadLength(int payloadLength) {
        int length = payloadLength;
        if (payloadLength % 4 > 0) {
            length += 4 - (payloadLength % 4);
        }
        return length;
    }
}
