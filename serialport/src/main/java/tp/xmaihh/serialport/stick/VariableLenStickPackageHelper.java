package tp.xmaihh.serialport.stick;


import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * 可变长度的粘包处理，使用于协议中有长度字段
 * 例：协议为:       type+dataLen+data+md5
 * type:命名类型，两个字节
 * dataLen:data字段的长度,两个字节
 * data:数据字段,长度不定，长度为dataLen
 * md5:md5字段，8个字节
 * 使用：1.byteOrder:首先确定大小端，ByteOrder.BIG_ENDIAN or ByteOrder.LITTLE_ENDIAN;
 * 2.lenSize:len字段的长度，这个例子为2
 * 3.lenIndex：len字段的位置，这个例子为2，因为len字段前面为type，它长度为2
 * 4.offset：整个包的长度-len，这个例子是，type+dataLen+md5 三个字段的长度，也就是2+2+8=12
 */
public class VariableLenStickPackageHelper implements AbsStickPackageHelper {
    private int offset = 0;//整体长度的偏移，比如有一个字段不算在len字段内
    private int lenIndex = 0;//长度字段的位置
    private int lenSize = 2;//len字段的长度，一般是short(2),int(4)
    private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;//大端还是小端
    private List<Byte> mBytes;
    private int lenStartIndex;//len字段的开始位置
    private int lenEndIndex;//len字段的结束位置，[lenStartIndex,lenEndIndex]

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
                    lenField[count - lenStartIndex] = temp;//保存len字段
                    if (count == lenEndIndex) {//len字段保存结束，需要解析出来具体的长度了
                        msgLen = getLen(lenField, byteOrder);
                    }
                }
                count++;
                mBytes.add(temp);
                if (msgLen != -1) {//已结解析出来长度
                    if (count == msgLen + offset) {
                        break;
                    } else if (count > msgLen + offset) {//error
                        len = -1;//标记为error
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
