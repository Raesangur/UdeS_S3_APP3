import java.io.File;  // Import the File class
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static java.lang.System.arraycopy;


public class ApplicationLayer extends Layer{
    private static ApplicationLayer instance;
    public static ApplicationLayer getInstance(){
        return instance == null ? instance = new ApplicationLayer() : instance;
    }

    @Override
    protected void ReceiveFromUp(byte[] PDU) {
        // NOTHING NOw
    }

    @Override
    protected void ReceiveFromDown(byte[] PDU) {
        System.out.println("Receiving");
        String title = new String(Arrays.copyOfRange(PDU, 0, 188), StandardCharsets.US_ASCII).trim();
        byte[] data_bytes = Arrays.copyOfRange(PDU, 188, PDU.length);
        try {
            String filePath = new File("").getAbsolutePath();
            File file = new File(filePath + "/dest/" + title);
            if(file.exists())
                file.delete();
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
            try (FileOutputStream fos = new FileOutputStream(file.getPath())) {
                System.out.println("writing stream");
                fos.write(data_bytes);
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void SendFile(String path) throws IOException {
        File file = new File(path);
        byte[] APDU;
        byte[] filename = file.getName().getBytes();
        Path filePath = file.toPath();
        byte[] fileBytes = Files.readAllBytes(filePath);
        APDU = new byte[188 + fileBytes.length];
        arraycopy(filename, 0, APDU, 0, filename.length);
        arraycopy(fileBytes, 0, APDU, 188, fileBytes.length);
        PassDown(APDU);
    }
}
