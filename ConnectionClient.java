/**
 * Created by Kevin McLaughlin on 1/21/2017.
 */
public interface ConnectionClient {

    public void connect();

    public void sendMessage(byte[] b);

    public byte[] receiveMessage(int size);

    public byte[] receiveMessage(int size, int timeout);

    public void disconnect();

}
