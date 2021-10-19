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
        physicalLayer.createReceptionThread(4445);
        physicalLayer.errorDelay = 10;
        physicalLayer.delay = 1;
        physicalLayer.start();
        physicalLayer.setDestPort(4446);
        physicalLayer.setDestAddress("localhost");
        applicaitonLayer.SendFile("guide3.pdf");

        System.out.println(physicalLayer.hashCode());
        while(physicalLayer.threadRunnint()) {

        }
    }
}