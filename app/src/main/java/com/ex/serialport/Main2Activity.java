package com.ex.serialport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

public class Main2Activity extends SerialPortActivity implements View.OnClickListener, View.OnLongClickListener {

    private EditText mReception;
    private EditText mEmission;
    private Button BtnSend;
    private Button BtnSelect;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_serial);
        mReception = findViewById(R.id.et_receive);
        mEmission = findViewById(R.id.et_send);
        BtnSend = findViewById(R.id.btn_send);
        BtnSelect = findViewById(R.id.btn_select);
        BtnSend.setOnClickListener(this);
        BtnSelect.setOnClickListener(this);
        mReception.setOnLongClickListener(this);
        sp = getSharedPreferences("com.ex.serialport_preferences", MODE_PRIVATE);
        String path = sp.getString("DEVICE", "/dev/ttyS1");
        int baudrate = Integer.decode(sp.getString("BAUDRATE", "115200"));
        setTitle("Device: " + path + "Baudrate: " + baudrate);
    }

    @Override
    protected void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (mReception != null) {
                    mReception.append(new String(buffer, 0, size));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                int i;
                CharSequence t = mEmission.getText();
                Log.d("chensy", "发送: " + t);
                char[] text = new char[t.length()];
                for (i = 0; i < t.length(); i++) {
                    text[i] = t.charAt(i);
                }
                try {
                    mOutputStream.write(new String(text).getBytes());
                    mOutputStream.write('\n');
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_select:
                startActivity(new Intent(Main2Activity.this, SerialPortPreferences.class));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.et_receive:
                mReception.setText("");
                break;
            default:
                break;
        }
        return false;
    }

}
