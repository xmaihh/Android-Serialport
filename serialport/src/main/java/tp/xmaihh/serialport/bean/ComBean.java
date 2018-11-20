package tp.xmaihh.serialport.bean;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ComBean {
    public byte[] bRec;
    public String sRecTime;
    public String sComPort;

    public ComBean(String sPort, byte[] buffer, int size) {
        this.sComPort = sPort;
        this.bRec = new byte[size];
        for (int i = 0; i < size; i++) {
            this.bRec[i] = buffer[i];
        }
        SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss");
        this.sRecTime = sDateFormat.format(new Date());
    }
}
