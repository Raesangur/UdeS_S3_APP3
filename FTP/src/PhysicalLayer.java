import java.io.*;
import java.net.*;

public class PhysicalLayer extends Layer {
    InetAddress address = null;
    int port = 0;
    protected ReceptionThread thread;

    // Singleton
    private PhysicalLayer instance;
    private PhysicalLayer(){}
    public Layer getInstance() {
        return instance == null ? new PhysicalLayer() : instance;
    }

    public void setDestAddress(String address) {
        try {
            this.address = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    public void setDestPort(int port) {
        this.port = port;
    }

    public void start() {
        thread.running = true;
    }
    public void stop() {
        thread.running = false;
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

        // send request
        DatagramPacket packet = new DatagramPacket(PDU, PDU.length, address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ReceiveFromDown(byte[] PDU) {
        PassUp(PDU);
    }


    private class ReceptionThread extends Thread{
        protected DatagramSocket socket = null;
        private Layer parent;
        public boolean running = true;

        public ReceptionThread(int port, Layer parent) throws IOException {
            super("FTP ReceptionThread");
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
