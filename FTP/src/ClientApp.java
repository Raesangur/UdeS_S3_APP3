import java.io.*;

public class ClientApp {
    public static void main(String[] args) throws IOException {
        String filename = args[0];
        String destination_ip = args[1];
        String listening_port = args[2];
        boolean addErrors = Boolean.parseBoolean(args[3]);

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
        physicalLayer.errorDelay = addErrors ? 10 : -1;
        physicalLayer.delay = 1;
        physicalLayer.start();
        physicalLayer.setDestPort(4446);
        physicalLayer.setDestAddress(destination_ip);
        applicationLayer.SendFile(filename);

        System.out.println(physicalLayer.hashCode());
        while(physicalLayer.threadRunnint()) {

        }
    }
}