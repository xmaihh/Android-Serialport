package tp.xmaihh.serialport.stick;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The sticky packet processing of specific characters,
 * one Byte[] at the beginning and the end, cannot be empty at the same time,
 * if one of them is empty, then the non-empty is used as the split marker
 * Example: The protocol is formulated as ^+data+$, starting with ^ and ending with $
 */
public class SpecifiedStickPackageHelper implements AbsStickPackageHelper {
    private final byte[] head;
    private final byte[] tail;
    private final List<Byte> bytes;
    private final int headLen;
    private final int tailLen;

    public SpecifiedStickPackageHelper(byte[] head, byte[] tail) {
        this.head = head;
        this.tail = tail;
        if (head == null || tail == null) {
            throw new IllegalStateException(" head or tail ==null");
        }
        if (head.length == 0 && tail.length == 0) {
            throw new IllegalStateException(" head and tail length==0");
        }
        headLen = head.length;
        tailLen = tail.length;
        bytes = new ArrayList<>();
    }

    private boolean endWith(Byte[] src, byte[] target) {
        if (src.length < target.length) {
            return false;
        }
        for (int i = 0; i < target.length; i++) {
            if (target[target.length - i - 1] != src[src.length - i - 1]) {
                return false;
            }
        }
        return true;
    }

    private byte[] getRangeBytes(List<Byte> list, int start, int end) {
        Byte[] temps = Arrays.copyOfRange(list.toArray(new Byte[0]), start, end);
        byte[] result = new byte[temps.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = temps[i];
        }
        return result;
    }

    @Override
    public byte[] execute(InputStream is) {
        bytes.clear();
        int len = -1;
        byte temp;
        int startIndex = -1;
        byte[] result = null;
        boolean isFindStart = false, isFindEnd = false;
        try {
            while ((len = is.read()) != -1) {
                temp = (byte) len;
                bytes.add(temp);
                Byte[] byteArray = bytes.toArray(new Byte[]{});
                if (headLen == 0 || tailLen == 0) {//Only head or tail markers
                    if (endWith(byteArray, head) || endWith(byteArray, tail)) {
                        if (startIndex == -1) {
                            startIndex = bytes.size() - headLen;
                        } else {
                            result = getRangeBytes(bytes, startIndex, bytes.size());
                            break;
                        }
                    }
                } else {
                    if (!isFindStart) {
                        if (endWith(byteArray, head)) {
                            startIndex = bytes.size() - headLen;
                            isFindStart = true;
                        }
                    } else if (!isFindEnd) {
                        if (endWith(byteArray, tail)) {
                            if (startIndex + headLen <= bytes.size() - tailLen) {
                                isFindEnd = true;
                                result = getRangeBytes(bytes, startIndex, bytes.size());
                                break;
                            }
                        }
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
        return result;
    }
}
