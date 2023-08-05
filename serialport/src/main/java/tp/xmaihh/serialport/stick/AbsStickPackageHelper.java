package tp.xmaihh.serialport.stick;

import java.io.InputStream;

/**
 * Accept the message, the helper of the sticky packet processing,
 * return the final data through the inputstream,
 * manually process the sticky packet, and the returned byte[] is the complete data we expect
 * Note: This method will be called repeatedly until a complete piece of data is parsed.
 * This method is synchronous, try not to do time-consuming operations, otherwise it will block reading data
 */
public interface AbsStickPackageHelper {
    byte[] execute(InputStream is);
}
