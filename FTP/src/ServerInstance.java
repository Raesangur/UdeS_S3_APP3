import java.io.IOException;

public class ServerInstance {
    private PhysicalLayer physicalLayer;

    /**
     * Constructor for the server
     * @param listening_port
     * @throws IOException
     */
    public ServerInstance(String listening_port) throws IOException {
        ServerInstanceBuild(listening_port);
    }

    /**
     * Build the server to ready the start of the listening for files
     * @param listening_port
     * @throws IOException
     */
    private void ServerInstanceBuild(String listening_port) throws IOException {
        ApplicationLayer applicationLayer = ApplicationLayer.getInstance();
        TransportLayer transportLayer = TransportLayer.getInstance();
        NetworkLayer networkLayer = NetworkLayer.getInstance();
        DataLinkLayer dataLinkLayer = DataLinkLayer.getInstance();
        physicalLayer = PhysicalLayer.getInstance();
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
        physicalLayer.setDestPort(4445);
        physicalLayer.setDestAddress("localhost");
    }

    /**
     * Start the server
     * @throws IOException
     */
    public void StartServer() throws IOException {
        physicalLayer.start();
        System.out.println("Running");
        System.out.println("Q: to kill");
        System.out.println("Then enter");
        while(physicalLayer.threadRunnint()) {
            int command = System.in.read();
            switch (command) {
                case 113:
                case 81:
                    System.exit(0);
                    break;
            }
        }
    }
}