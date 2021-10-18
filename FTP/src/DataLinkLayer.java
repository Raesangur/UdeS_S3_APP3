import java.util.zip.CRC32;
import static java.lang.System.arraycopy;

public class DataLinkLayer extends Layer {
    // Statistics
    private int crcErrors = 0;
    private int receivedPackets = 0;
    private int transmittedPackets = 0;

    // Singleton
    private DataLinkLayer instance;
    private DataLinkLayer() {}
    public DataLinkLayer getInstance() {
        return instance == null ? new DataLinkLayer() : instance;
    }

    public void Reset() {
        crcErrors = 0;
        receivedPackets = 0;
        transmittedPackets = 0;
    }


    @Override
    protected void ReceiveFromDown(byte[] PDU) {
        // Calculate & Check CRC
        CRC32 crc = new CRC32();
        crc.update(PDU);
        if (crc.getValue() != 0) {  // Error in CRC
            crcErrors++;
            return;                 // Drop packet
        }

        // Extract data from PDU
        byte[] paquet = new byte[PDU.length - 4];
        arraycopy(PDU, 4, paquet, 0, paquet.length);

        // Send PDU to network layer
        receivedPackets++;
        PassUp(paquet);
    }

    @Override
    protected void ReceiveFromUp(byte[] PDU) {
        // Allocate new PDU
        byte[] trame = new byte[PDU.length + 4];

        // Calculate CRC using polynomial 0x82608EDB (default)
        CRC32 crc = new CRC32();
        crc.update(PDU);
        long crcValue = crc.getValue();
        byte[] CRCBytes = new byte[] {
                (byte) (crcValue >> 24),
                (byte) (crcValue >> 16),
                (byte) (crcValue >> 8),
                (byte) crcValue};
        arraycopy(CRCBytes, 0, trame, 0, CRCBytes.length);

        // Copy PDU into trame
        arraycopy(PDU, 0, trame, 4, PDU.length);

        // Send PDU to physical layer
        transmittedPackets++;
        PassDown(trame);
    }
}
