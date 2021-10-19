public abstract class Layer {
    private Layer layerUp;
    private Layer layerDown;

    protected abstract void ReceiveFromUp(byte[] PDU);
    protected abstract void ReceiveFromDown(byte[] PDU) throws TransmissionErrorException;

    protected void PassUp(byte[] PDU) throws TransmissionErrorException {
        layerUp.ReceiveFromDown(PDU);
    }
    protected void PassDown(byte[] PDU) {
        layerDown.ReceiveFromUp(PDU);
    }

    public void setUpLayer(Layer upLayer) {
        layerUp = upLayer;
    }
    public void setDownLayer(Layer downLayer) {
        layerDown = downLayer;
    }
}
