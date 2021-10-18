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
    protected void ReceiveFromDown(byte[] PDU) {
        PassUp(PDU);
    }
}
