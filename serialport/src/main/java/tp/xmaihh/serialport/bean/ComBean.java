package tp.xmaihh.serialport.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ComBean implements Parcelable {
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

    protected ComBean(Parcel in) {
        bRec = in.createByteArray();
        sRecTime = in.readString();
        sComPort = in.readString();
    }

    public static final Creator<ComBean> CREATOR = new Creator<ComBean>() {
        @Override
        public ComBean createFromParcel(Parcel in) {
            return new ComBean(in);
        }

        @Override
        public ComBean[] newArray(int size) {
            return new ComBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(bRec);
        dest.writeString(sRecTime);
        dest.writeString(sComPort);
    }
}
