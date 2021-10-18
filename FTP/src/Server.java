import java.io.IOException;

public class Server {
    public static void main(String[] args) throws IOException {
        TransportLayer transportLayer = TransportLayer.getInstance();
        NetworkLayer networkLayer = NetworkLayer.getInstance();
        DataLinkLayer dataLinkLayer = DataLinkLayer.getInstance();
        PhysicalLayer physicalLayer = PhysicalLayer.getInstance();
        ApplicationLayer applicaitonLayer = ApplicationLayer.getInstance();
        physicalLayer.setUpLayer(dataLinkLayer);
        dataLinkLayer.setUpLayer(networkLayer);
        dataLinkLayer.setDownLayer(physicalLayer);
        networkLayer.setUpLayer(transportLayer);
        networkLayer.setDownLayer(dataLinkLayer);
        transportLayer.setDownLayer(networkLayer);
        transportLayer.setUpLayer(applicaitonLayer);


        // set server
        physicalLayer.createReceptionThread(4445);
        physicalLayer.start();

        System.out.println("Running");
        while(physicalLayer.threadRunnint()) {

        }
        System.out.println("Finished");
    }
}