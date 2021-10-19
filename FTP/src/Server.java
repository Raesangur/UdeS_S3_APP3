import java.io.IOException;
import java.util.LinkedList;

public class Server {
    public static void main(String[] args) throws IOException {
        String listening_port = args[0];

        TransportLayer transportLayer = TransportLayer.getInstance();
        NetworkLayer networkLayer = NetworkLayer.getInstance();
        DataLinkLayer dataLinkLayer = DataLinkLayer.getInstance();
        PhysicalLayer physicalLayer = PhysicalLayer.getInstance();
        ApplicationLayer applicationLayer = ApplicationLayer.getInstance();
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
        physicalLayer.start();
        physicalLayer.setDestPort(4445);
        physicalLayer.setDestAddress("localhost");

        System.out.println("Running");
        while(physicalLayer.threadRunnint()) {

        }
        System.out.println("Finished");
    }
}