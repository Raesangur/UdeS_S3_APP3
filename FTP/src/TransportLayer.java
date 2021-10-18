import java.nio.charset.StandardCharsets;

import static java.lang.System.arraycopy;

public class TransportLayer extends Layer {
    private final char CODE_START = 'd';
    private final char CODE_END = 'f';
    private final char CODE_NORMAL = ' ';
    private final char CODE_ACK = 'a';

    private final int OFFSET = 12;
    private final int SIZE = 188;
    private final int SIZE_HEADER_POS = 9;
    private final int SEQ_HEADER_POS = 1;

    private byte[][] TPDU;

    @Override
    protected void ReceiveFromUp(byte[] PDU) {
        int count = PDU.length / SIZE;
        TPDU = new byte[count][200];

        // Division of PDU for size of 200 each & end send to Layer
        for(int i = 0; i < count; i++) {
            arraycopy(PDU, i * SIZE, TPDU[i], OFFSET, SIZE);

            int taille = 200;
            if (i == count - 1) {
                taille = PDU.length % SIZE;
            }

            char code = CODE_NORMAL;
            if (i == 0) {
                code = CODE_START;
            } else if (i == count - i) {
                code = CODE_END;
            }

            // Copy data size as ASCII in header
            arraycopy(convert(taille, 3), 0, TPDU[i], SIZE_HEADER_POS, 3);
            arraycopy(convert(i, 8), 0, TPDU[i], SEQ_HEADER_POS, 8);
            TPDU[i][0] = (byte) code;       // Assuming sizeof(char) == sizeof(byte)

            // Send lower Layer
            PassDown(TPDU[i]);
        }
    }

    @Override
    protected void ReceiveFromDown(byte[] PDU) {

    }

    private byte[] convert(int data, int size) {
        String converted = Integer.toString(data);
        byte[] converted2 = converted.getBytes(StandardCharsets.US_ASCII);

        // Copy data with padding on the left
        byte[] newData = new byte[size];
        arraycopy(converted2, 0, newData, size - converted2.length, converted2.length);

        return newData;
    }
}
