import java.io.*;
import java.net.*;

public class PhysicalLayer extends Layer {
    InetAddress address = null;
    int port = 0;
    protected ReceptionThread thread;
    public int delay = 0;
    public int errorDelay = -1;
    public int packetSent = 0;

    // Singleton
    private static PhysicalLayer instance;
    private PhysicalLayer(){}
    static public PhysicalLayer getInstance() {
        return instance == null ? new PhysicalLayer() : instance;
    }

    public void setDestAddress(String address) {
        try {
            this.address = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    public void setDestAddress(InetAddress address) {
        try {
            this.address = address;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setDestPort(int port) {
        this.port = port;
    }

    public void start() {
        thread.running = true;
        thread.start();
    }
    public void stop() {
        thread.running = false;
    }
    public boolean threadRunnint() {
        return thread.running;
    }

    @Override
    protected void ReceiveFromUp(byte[] PDU) {
        // get a datagram socket
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // shift bit for error
        packetSent++;
        if(packetSent == errorDelay)
            PDU[10] <<= 1;

        // send request
        DatagramPacket packet = new DatagramPacket(PDU, PDU.length, address, port);
        try {
            socket.send(packet);
            Thread.sleep(delay);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ReceiveFromDown(byte[] PDU) {
        PassUp(PDU);
    }

    public void createReceptionThread(int port) throws IOException {
        this.thread = new ReceptionThread(port, this);
    }


    private class ReceptionThread extends Thread{
        protected DatagramSocket socket = null;
        private PhysicalLayer parent;
        public boolean running = true;

        public ReceptionThread(int port, PhysicalLayer parent) throws IOException {
            super("FTP ReceptionThread " + Math.random());
            socket = new DatagramSocket(port);
            this.parent = parent;
        }

        public void run() {
            while (running) {
                try {
                    byte[] buf = new byte[204];

                    // receive request
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);

                    // set to client
                    // Send packet data to parent
                    parent.ReceiveFromDown(packet.getData());
                } catch (IOException e) {
                    running = false;
                    e.printStackTrace();
                }
            }
            socket.close();
        }
    }

}
