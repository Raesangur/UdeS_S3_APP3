import java.io.IOException;

public class ClientAppInstance {
    PhysicalLayer physicalLayer;
    ApplicationLayer applicationLayer;
    String filename;

    /**
     * Constructor for client which call ClientBuild() to prep the client
     * @param filename
     * @param destination_ip
     * @param listening_port
     * @param addErrors
     * @throws IOException
     * @throws InterruptedException
     */
    public ClientAppInstance(String filename, String destination_ip, String listening_port, boolean addErrors) throws IOException, InterruptedException {
        this.filename = filename;
        ClientInstanceBuild(destination_ip, listening_port, addErrors);
    }

    /**
     * Build the client ready to start the communication
     * @param destination_ip
     * @param listening_port
     * @param addErrors
     * @throws IOException
     */
    public void ClientInstanceBuild(String destination_ip, String listening_port, boolean addErrors) throws IOException {
        TransportLayer transportLayer = TransportLayer.getInstance();
        NetworkLayer networkLayer = NetworkLayer.getInstance();
        DataLinkLayer dataLinkLayer = DataLinkLayer.getInstance();
        physicalLayer = PhysicalLayer.getInstance();
        applicationLayer = ApplicationLayer.getInstance();
        physicalLayer.setUpLayer(dataLinkLayer);
        dataLinkLayer.setUpLayer(networkLayer);
        dataLinkLayer.setDownLayer(physicalLayer);
        networkLayer.setUpLayer(transportLayer);
        networkLayer.setDownLayer(dataLinkLayer);
        transportLayer.setDownLayer(networkLayer);
        transportLayer.setUpLayer(applicationLayer);
        applicationLayer.setDownLayer(transportLayer);

        // set server
        physicalLayer.createReceptionThread(Integer.parseInt(listening_port));
        physicalLayer.errorDelay = addErrors ? 10 : -1;
        physicalLayer.delay = 1;
        physicalLayer.setDestPort(4446);
        physicalLayer.setDestAddress(destination_ip);
    }

    /**
     * Start sending the file to the server
     * @throws IOException
     * @throws InterruptedException
     */
    public void ClientStart() throws IOException, InterruptedException {
        System.out.println("Client Start");
        physicalLayer.start();
        applicationLayer.SendFile(filename);
    }
}
