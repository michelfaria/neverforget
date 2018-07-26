package never;

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public final class IO {

    public static Image readImageFromFile(File file) throws IOException {
        return ImageIO.read(file);
    }

    public static byte[] readFile(File file) throws IOException {
        ByteArrayOutputStream bos = null;
        InputStream is = null;
        try {
            byte[] baBuf = new byte[8192];
            bos = new ByteArrayOutputStream();
            is = new FileInputStream(file);
            int read = 0;
            while ((read = is.read(baBuf)) != -1) {
                bos.write(baBuf, 0, read);
            }
            return bos.toByteArray();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
