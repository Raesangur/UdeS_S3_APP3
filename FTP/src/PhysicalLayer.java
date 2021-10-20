import java.io.*;
import java.net.*;

/**
 * Wrapper around the datagram Berkeley sockets.
 */
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

    /**
     * Configure destination address for transmission
     * @param address String representing the address (either an IP or a
     *                DNS-resolvable string)
     */
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

    /**
     * Configure port for transmission
     */
    public void setDestPort(int port) {
        this.port = port;
    }

    /**
     * Start reception thread
     */
    public void start() {
        thread.running = true;
        thread.start();
    }

    /**
     * Stop reception thread
     */
    public void stop() {
        thread.running = false;
        thread.stop();
    }

    /**
     * Check if reception thread is running
     * @return  True if reception thread is running
     *          False otherwise
     */
    public boolean threadRunnint() {
        return thread.running;
    }

    /**
     * Receive data to transmit from the DataLink Layer.
     * Transmits the data at the select address and port.
     * Can inject an error as a bit shift on the selected paquet.
     * Adds a 1ms delay after each paquet.
     */
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

    /**
     * Transmits received paquet to the DataLink Layer.
     */
    @Override
    public void ReceiveFromDown(byte[] PDU) throws TransmissionErrorException {
        PassUp(PDU);
    }

    /**
     * Create a reception thread on the selected port.
     */
    public void createReceptionThread(int port) throws IOException {
        this.thread = new ReceptionThread(port, this);
    }


    /**
     * Simple thread listening to a reception socket and passing its data to
     * the Physical Layer to be retransmitted to the DataLink Layer.
     */
    private class ReceptionThread extends Thread{
        protected DatagramSocket socket = null;
        private PhysicalLayer parent;
        public boolean running = true;

        public ReceptionThread(int port, PhysicalLayer parent) throws IOException {
            super("FTP ReceptionThread " + Math.random());
            socket = new DatagramSocket(port);
            this.parent = parent;
        }

        /**
         * Listens continuously on the reception socket.
         */
        public void run() {
            while (running) {
                try {
                    byte[] buf = new byte[204];

                    // receive request
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);

                    // Send packet data to parent
                    parent.ReceiveFromDown(packet.getData());
                } catch (IOException | TransmissionErrorException e) {
                    running = false;
                    socket.close();
                    System.out.println(e.getLocalizedMessage());
                }
            }
            socket.close();
        }
    }

}
