package tp.xmaihh.serialport.stick;

import android.os.SystemClock;

import java.io.IOException;
import java.io.InputStream;

/**
 * The simplest thing to do is not to deal with sticky packets,
 * read directly and return as much as InputStream.available() reads
 */
public class BaseStickPackageHelper implements AbsStickPackageHelper {
    public BaseStickPackageHelper() {
    }

    @Override
    public byte[] execute(InputStream is) {
        try {
            int available = is.available();
            if (available > 0) {
                byte[] buffer = new byte[available];
                int size = is.read(buffer);
                if (size > 0) {
                    return buffer;
                }
            } else {
                SystemClock.sleep(50);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
