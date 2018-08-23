package com.ex.serialport;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;

public class SerialPortUtil {
    private static volatile SerialPortUtil serialPortUtil;

    public static SerialPortUtil getInstance() {
        if (serialPortUtil == null) {
            synchronized (SerialPortUtil.class) {
                if (serialPortUtil == null) {
                    serialPortUtil = new SerialPortUtil();
                }
            }
        }
        return serialPortUtil;
    }

    private SerialPortUtil() {

    }

    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;

    public SerialPort getSerialPort(Context context) throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            /* Read serial port parameters */
            SharedPreferences sp = context.getSharedPreferences("android_serialport_api.sample_preferences", Context.MODE_PRIVATE);
            String path = sp.getString("DEVICE", "");
            int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

            /* Check parameters */
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

            /* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
        }
        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }
}
