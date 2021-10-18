public class NetworkLayer extends Layer {

    @Override
    protected void ReceiveFromUp(byte[] PDU) {
        PassDown(PDU);
    }

    @Override
    protected void ReceiveFromDown(byte[] PDU) {
        PassUp(PDU);
    }
}
