/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android_serialport_api;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort {

    private static final String TAG = "SerialPort";

    /**
     * Commonly used serial port baudrate
     */
    public enum BAUDRATE {
        B0(0),
        B50(50),
        B75(75),
        B110(110),
        B134(134),
        B150(150),
        B200(200),
        B300(300),
        B600(600),
        B1200(1200),
        B1800(1800),
        B2400(2400),
        B4800(4800),
        B9600(9600),
        B19200(19200),
        B38400(38400),
        B57600(57600),
        B115200(115200),
        B230400(230400),
        B460800(460800),
        B500000(500000),
        B576000(576000),
        B921600(921600),
        B1000000(1000000),
        B1152000(1152000),
        B1500000(1500000),
        B2000000(2000000),
        B2500000(2500000),
        B3000000(3000000),
        B3500000(3500000),
        B4000000(4000000);

        int baudrate;

        BAUDRATE(int baudrate) {
            this.baudrate = baudrate;
        }

        int getBaudrate() {
            return this.baudrate;
        }

    }

    /**
     * Serial port stop bit
     */
    public enum STOPB {
        B1(1),
        B2(2);

        int stopBit;

        STOPB(int stopBit) {
            this.stopBit = stopBit;
        }

        public int getStopBit() {
            return this.stopBit;
        }

    }

    /**
     * Serial port data bits
     */
    public enum DATAB {
        CS5(5),
        CS6(6),
        CS7(7),
        CS8(8);

        int dataBit;

        DATAB(int dataBit) {
            this.dataBit = dataBit;
        }

        public int getDataBit() {
            return this.dataBit;
        }
    }

    /**
     * Serial port parity
     */
    public enum PARITY {
        NONE(0),
        ODD(1),
        EVEN(2),
        SPACE(3),
        MARK(4);

        int parity;

        PARITY(int parity) {
            this.parity = parity;
        }

        public int getParity() {
            return this.parity;
        }
    }

    /**
     * Serial port flow type
     */
    public enum FLOWCON {
        NONE(0),
        HARD(1),
        SOFT(2);

        int flowCon;

        FLOWCON(int flowCon) {
            this.flowCon = flowCon;
        }

        public int getFlowCon() {
            return this.flowCon;
        }
    }


    /*
     * Do not remove or rename the field mFd: it is used by native method close();
     */
    private final FileDescriptor mFd;
    private final FileInputStream mFileInputStream;
    private final FileOutputStream mFileOutputStream;

    public SerialPort(File device, int baudrate, int stopBits, int dataBits, int parity, int flowCon, int flags) throws SecurityException, IOException {

        /* Check access permission */
        if (!device.canRead() || !device.canWrite()) {
            try {
                /* Missing read/write permission, trying to chmod the file */
                Process su;
                su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead()
                        || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }

        mFd = open(device.getAbsolutePath(), baudrate, stopBits, dataBits, parity, flowCon, flags);
        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    // Getters and setters
    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    /**
     * The serial port has 5 parameters: serial device name, baud rate, check bit, data bit, stop bit
     * Among them, the check bit is generally defaulted to the NONE, the data bit is generally defaulted to 8, and the stop bit is defaulted to 1
     *
     * @param path     to the data pair of the serial device
     * @param baudrate {@link BAUDRATE} baudrate
     * @param stopBits {@link STOPB} stop bit
     * @param dataBits {@link DATAB} data bits
     * @param parity   {@link PARITY} check digit
     * @param flowCon  {@link FLOWCON} flow control
     * @param flags    O_RDWR read and write mode to open | O_NOCTTY Do not allow process management serial port | O_NDELAY Non-blocking
     * @return
     */
    private native static FileDescriptor open(String path, int baudrate, int stopBits, int dataBits, int parity, int flowCon, int flags);

    public native void close();

    static {
        System.loadLibrary("serialport");
    }
}
