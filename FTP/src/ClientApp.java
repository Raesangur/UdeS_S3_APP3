import java.io.*;

public class ClientApp {
    public static void main(String[] args) throws IOException, InterruptedException {
        String filename = args[0];
        String destination_ip = args[1];
        String listening_port = args[2];
        boolean addErrors = Boolean.parseBoolean(args[3]);

        ClientAppInstance instance = new ClientAppInstance(filename,destination_ip,listening_port,addErrors);
        instance.ClientStart();
    }
}