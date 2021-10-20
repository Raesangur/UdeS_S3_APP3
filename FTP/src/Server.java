import java.io.IOException;

public class Server {
    public static void main(String[] args) throws IOException {
        String listening_port = args[0];
        ServerInstance instance = new ServerInstance(listening_port);
        instance.StartServer();
        System.out.println("Finished");
    }
}