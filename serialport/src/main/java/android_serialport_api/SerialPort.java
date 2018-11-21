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
     * 串口波特率定义
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
     * 串口停止位定义
     */
    public enum STOPB {
        /**
         * 1位停止位
         */
        B1(1),
        /**
         * 2位停止位
         */
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
     * 串口数据位定义
     */
    public enum DATAB {
        /**
         * 5位数据位
         */
        CS5(5),
        /**
         * 6位数据位
         */
        CS6(6),
        /**
         * 7位数据位
         */
        CS7(7),
        /**
         * 8位数据位
         */
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
     * 串口校验位定义
     */
    public enum PARITY {
        /**
         * 无奇偶校验
         */
        NONE(0),
        /**
         * 奇校验
         */
        ODD(1),
        /**
         * 偶校验
         */
        EVEN(2);

        int parity;

        PARITY(int parity) {
            this.parity = parity;
        }

        public int getParity() {
            return this.parity;
        }
    }

    /**
     * 串口流控定义
     */
    public enum FLOWCON {
        /**
         * 不使用流控
         */
        NONE(0),
        /**
         * 硬件流控
         */
        HARD(1),
        /**
         * 软件流控
         */
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
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    public SerialPort(File device, int baudrate, int stopBits, int dataBits, int parity, int flowCon, int flags) throws SecurityException, IOException {

        /* Check access permission */  // 检查是否获取了指定串口的读写权限
        if (!device.canRead() || !device.canWrite()) {
            try {
                /* Missing read/write permission, trying to chmod the file */
                // 如果没有获取指定串口的读写权限，则通过挂在到linux的方式修改串口的权限为可读写
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

    // JNI
    // JNI：调用java本地接口，实现串口的打开和关闭

    /**
     * 串口有五个重要的参数：串口设备名，波特率，检验位，数据位，停止位
     * 其中检验位一般默认位NONE,数据位一般默认为8，停止位默认为1
     *
     * @param path     串口设备的据对路径
     * @param baudrate {@link BAUDRATE}波特率
     * @param stopBits {@link STOPB}停止位
     * @param dataBits {@link DATAB}数据位
     * @param parity   {@link PARITY}校验位
     * @param flowCon  {@link FLOWCON}流控
     * @param flags    O_RDWR  读写方式打开 | O_NOCTTY  不允许进程管理串口 | O_NDELAY   非阻塞
     * @return
     */
    private native static FileDescriptor open(String path, int baudrate, int stopBits, int dataBits, int parity, int flowCon, int flags); //打开串口

    public native void close(); //关闭串口

    static {
        System.loadLibrary("serialport"); // 载入底层C文件 so库链接文件
    }
}
