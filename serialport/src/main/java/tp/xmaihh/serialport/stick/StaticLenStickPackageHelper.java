package tp.xmaihh.serialport.stick;

import java.io.IOException;
import java.io.InputStream;

/**
 * 定长的粘包处理
 * 例：协议规定每次包的长度为 16
 */
public class StaticLenStickPackageHelper implements AbsStickPackageHelper {
    private int stackLen = 16;

    public StaticLenStickPackageHelper(int stackLen) {
        this.stackLen = stackLen;
    }

    @Override
    public byte[] execute(InputStream is) {
        int count = 0;
        int len = -1;
        byte temp;
        byte[] result = new byte[stackLen];
        try {
            while (count < stackLen && (len = is.read()) != -1) {
                temp = (byte) len;
                result[count] = temp;
                count++;
            }
            if (len == -1) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }
}
