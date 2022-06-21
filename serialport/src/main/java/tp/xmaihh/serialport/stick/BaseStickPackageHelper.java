package tp.xmaihh.serialport.stick;

import android.os.SystemClock;

import java.io.IOException;
import java.io.InputStream;

/**
 * 最简单的做法，不处理粘包，直接读取返回，InputStream.available()读多少返回多少
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
