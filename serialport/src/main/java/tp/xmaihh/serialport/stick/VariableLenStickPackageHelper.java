package tp.xmaihh.serialport.stick;


import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Variable-length sticky packet processing, used in the protocol with a length field
 * Example: The protocol is: type+dataLen+data+md5
 * type: Named type, two bytes
 * dataLen: The length of the data field, two bytes
 * data: Data field, variable length, length dataLen
 * md5: md5 field, 8 bytes
 * Use: 1.byteOrder: first determine the big and small ends, ByteOrder.BIG_ENDIAN or ByteOrder.LITTLE_ENDIAN;
 * 2.lenSize: The length of the len field, 2 in this example
 * 3.lenIndex: The position of the len field, 2 in this example, because the len field is preceded by type, and its length is 2
 * 4.offset: the length of the entire package -len, this example is the length of the three fields of type+dataLen+md5, that is, 2+2+8=12
 */
public class VariableLenStickPackageHelper implements AbsStickPackageHelper {
    private int offset = 0;
    private int lenIndex = 0;
    private int lenSize = 2;
    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    private final List<Byte> mBytes;
    private final int lenStartIndex;
    private final int lenEndIndex;

    public VariableLenStickPackageHelper(ByteOrder byteOrder, int lenSize, int lenIndex, int offset) {
        this.byteOrder = byteOrder;
        this.lenSize = lenSize;
        this.offset = offset;
        this.lenIndex = lenIndex;
        mBytes = new ArrayList<>();
        lenStartIndex = lenIndex;
        lenEndIndex = lenIndex + lenSize - 1;
        if (lenStartIndex > lenEndIndex) {
            throw new IllegalStateException("lenStartIndex>lenEndIndex");
        }
    }

    private int getLen(byte[] src, ByteOrder order) {
        int re = 0;
        if (order == ByteOrder.BIG_ENDIAN) {
            for (byte b : src) {
                re = (re << 8) | (b & 0xff);
            }
        } else {
            for (int i = src.length - 1; i >= 0; i--) {
                re = (re << 8) | (src[i] & 0xff);
            }
        }
        return re;
    }

    @Override
    public byte[] execute(InputStream is) {
        mBytes.clear();
        int count = 0;
        int len = -1;
        byte temp;
        byte[] result;
        int msgLen = -1;
        byte[] lenField = new byte[lenSize];
        try {
            while ((len = is.read()) != -1) {
                temp = (byte) len;
                if (count >= lenStartIndex && count <= lenEndIndex) {
                    lenField[count - lenStartIndex] = temp;
                    if (count == lenEndIndex) {
                        msgLen = getLen(lenField, byteOrder);
                    }
                }
                count++;
                mBytes.add(temp);
                if (msgLen != -1) {
                    if (count == msgLen + offset) {
                        break;
                    } else if (count > msgLen + offset) {
                        len = -1;
                        break;
                    }
                }
            }
            if (len == -1) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        result = new byte[mBytes.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = mBytes.get(i);
        }
        return result;
    }
}
