/**
 * Main Layer Class that the other layers (Physical, DataLink, Network,
 * Transport & Application) inherit from.
 */
public abstract class Layer {
    private Layer layerUp;
    private Layer layerDown;

    /**
     * Receive data from the upper layer
     */
    protected abstract void ReceiveFromUp(byte[] PDU);
    /**
     * Receive data from the lower layer
     */
    protected abstract void ReceiveFromDown(byte[] PDU) throws TransmissionErrorException;

    /**
     * Send data to the upper layer
     */
    protected void PassUp(byte[] PDU) throws TransmissionErrorException {
        layerUp.ReceiveFromDown(PDU);
    }
    /**
     * Send data to the lower layer
     */
    protected void PassDown(byte[] PDU) {
        layerDown.ReceiveFromUp(PDU);
    }

    /**
     * Configure upper layer
     */
    public void setUpLayer(Layer upLayer) {
        layerUp = upLayer;
    }
    /**
     * Configure lower layer
     */
    public void setDownLayer(Layer downLayer) {
        layerDown = downLayer;
    }
}
