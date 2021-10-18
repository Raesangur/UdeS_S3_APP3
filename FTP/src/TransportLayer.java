import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import static java.util.Arrays.fill;
import java.util.LinkedList;

import static java.lang.System.arraycopy;

public class TransportLayer extends Layer {
    private static TransportLayer instance;

    private final char CODE_START = 'd';
    private final char CODE_END = 'f';
    private final char CODE_NORMAL = ' ';
    private final char CODE_ACK = 'a';
    private final char CODE_RESEND = 'r';

    private final int OFFSET = 11;
    private final int SIZE = 188;
    private final int SIZE_HEADER_POS = 9;
    private final int SEQ_HEADER_POS = 1;

    private int errors;

    private byte[][] TPDU;
    private LinkedList<byte[]> receiveBuffer;

    public static TransportLayer getInstance() {
        return instance == null ? instance = new TransportLayer() : instance;
    }

    @Override
    protected void ReceiveFromUp(byte[] PDU) {
        int count = (int) Math.ceil((double) PDU.length / SIZE);
        TPDU = new byte[count][200];

        // Division of PDU for size of 200 each & end send to Layer
        for(int i = 0; i < count; i++) {
            int taille = SIZE;
            if (i == count - 1) {
                taille = PDU.length % SIZE;
            }

            arraycopy(PDU, i * SIZE, TPDU[i], OFFSET + 1, taille - OFFSET);


            char code = CODE_NORMAL;
            if (i == 0) {
                code = CODE_START;
            } else if (i == count - i) {
                code = CODE_END;
            }

            // Copy data size as ASCII in header
            TPDU[i][0] = (byte) code;       // Assuming sizeof(char) == sizeof(byte)
            arraycopy(convertIntToASCII(i, 8), 0, TPDU[i], SEQ_HEADER_POS, 8); // SEQUENCE
            arraycopy(convertIntToASCII(taille, 3), 0, TPDU[i], SIZE_HEADER_POS, 3); // SIZE

            // Send lower Layer
            PassDown(TPDU[i]);
        }
    }

    @Override
    protected void ReceiveFromDown(byte[] PDU) {
        System.out.println("transport layer received");

        byte[] seq_bytes = Arrays.copyOfRange(PDU, SEQ_HEADER_POS, SIZE_HEADER_POS);
        byte[] size_bytes = Arrays.copyOfRange(PDU, SIZE_HEADER_POS, OFFSET + 1);

        char code = (char) PDU[0];
        int seq = convertAsciiToInt(seq_bytes);
        int size = convertAsciiToInt(size_bytes);

        byte[] data_bytes = Arrays.copyOfRange(PDU, OFFSET + 1, OFFSET + size - 1);

        switch (code){
            case CODE_START:
                // start of the communication & reset receive Buffer
                receiveBuffer = new LinkedList<>();
                receiveBuffer.add(0,  data_bytes); // file name
                break;

            case CODE_END:
                receiveBuffer.add(seq, data_bytes);
                int arrayL = (receiveBuffer.size() - 1) * SIZE + data_bytes.length;
                byte[] passUpBuffer = new byte[arrayL];
                int count = 0;
                for (byte[] arr : receiveBuffer) {
                    arraycopy(arr, 0, passUpBuffer, count, arr.length);
                    count += arr.length;
                }
                PassUp(passUpBuffer);
                break;

            case CODE_NORMAL:
                try {
                    if (receiveBuffer.get(seq - 1) == null) {
                        byte[] rPDU = createResendPDU(seq - 1);
                        PassDown(rPDU);
                    }
                }catch (IndexOutOfBoundsException e) {
                    errors++;
                    if(errors >= 3) {
                        // TODO: throw error
                    }

                    byte[] rPDU = createResendPDU(seq - 1);
                    PassDown(rPDU);
                }
                receiveBuffer.add(seq, data_bytes);
                byte[] ackPDU = createAckPDU(seq);
                PassDown(ackPDU);
                break;
            case CODE_ACK:
                break;
            case CODE_RESEND:
                PassDown(TPDU[seq]);
                break;
        }
    }

    private byte[] createResendPDU(int seq) {
        int taille = 0;
        byte[] resendPDU = new byte[200];

        // Copy data size as ASCII in header
        resendPDU[0] = (byte) CODE_RESEND;       // Assuming sizeof(char) == sizeof(byte)
        arraycopy(convertIntToASCII(seq, 8), 0, resendPDU, SEQ_HEADER_POS, 8); // SEQUENCE
        arraycopy(convertIntToASCII(taille, 3), 0, resendPDU, SIZE_HEADER_POS, 3); // SIZE

        // Send lower Layer
        return resendPDU;
    }

    private byte[] createAckPDU(int seq) {
        byte[] ackPDU = new byte[200];

        // Copy data size as ASCII in header
        ackPDU[0] = (byte) CODE_ACK;       // Assuming sizeof(char) == sizeof(byte)
        arraycopy(convertIntToASCII(seq, 8), 0, ackPDU, SEQ_HEADER_POS, 8); // SEQUENCE
        arraycopy(convertIntToASCII(0, 3), 0, ackPDU, SIZE_HEADER_POS, 3); // SIZE

        // Send lower Layer
        return ackPDU;
    }

    private byte[] convertIntToASCII(int data, int size) {
        String converted = Integer.toString(data);
        byte[] converted2 = converted.getBytes(StandardCharsets.US_ASCII);

        // Copy data with padding on the left
        byte[] newData = new byte[size];
        fill(newData, (byte) '0');
        arraycopy(converted2, 0, newData, size - converted2.length, converted2.length);

        return newData;
    }

    private int convertAsciiToInt(byte[] data) {
        String data_string = new String(data);
        return Integer.parseInt(removeLeadingZeros(data_string));
    }

    public static String removeLeadingZeros(String str)
    {
        String regex = "^0+(?!$)";
        str = str.replaceAll(regex, "");

        return str;
    }
}
