/**
 * Empty layer, simply forwarding data from the adjacent layers to the next.
 */
public class NetworkLayer extends Layer {
    private static NetworkLayer instance;
    public static NetworkLayer getInstance(){
        return instance == null ? instance = new NetworkLayer() : instance;
    }

    @Override
    protected void ReceiveFromUp(byte[] PDU) {
        PassDown(PDU);
    }

    @Override
    protected void ReceiveFromDown(byte[] PDU) throws TransmissionErrorException {
        PassUp(PDU);
    }
}
