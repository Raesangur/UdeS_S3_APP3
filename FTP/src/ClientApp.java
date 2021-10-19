import java.io.*;

public class ClientApp {
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
        applicaitonLayer.setDownLayer(transportLayer);

        // set server
        physicalLayer.createReceptionThread(4446);
        physicalLayer.start();
        physicalLayer.setDestPort(4445);
        physicalLayer.setDestAddress("localhost");
        applicaitonLayer.SendFile("bonmatin.txt");
    }
}