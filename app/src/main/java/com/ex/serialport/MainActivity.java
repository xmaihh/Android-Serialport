package com.ex.serialport;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ex.serialport.adapter.LogListAdapter;
import com.ex.serialport.adapter.SpAdapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android_serialport_api.SerialPortFinder;
import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;
import tp.xmaihh.serialport.utils.ByteUtil;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recy;
    private Spinner spSerial;
    private EditText edInput;
    private Button btSend;
    private RadioGroup radioGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private SerialPortFinder serialPortFinder;
    private SerialHelper serialHelper;
    private Spinner spBote;
    private Button btOpen;
    private LogListAdapter logListAdapter;
    private Spinner spDatab;
    private Spinner spParity;
    private Spinner spStopb;
    private Spinner spFlowcon;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serialHelper.close();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recy = (RecyclerView) findViewById(R.id.recyclerView);
        spSerial = (Spinner) findViewById(R.id.sp_serial);
        edInput = (EditText) findViewById(R.id.ed_input);
        btSend = (Button) findViewById(R.id.btn_send);
        spBote = (Spinner) findViewById(R.id.sp_baudrate);
        btOpen = (Button) findViewById(R.id.btn_open);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);

        spDatab = (Spinner) findViewById(R.id.sp_databits);
        spParity = (Spinner) findViewById(R.id.sp_parity);
        spStopb = (Spinner) findViewById(R.id.sp_stopbits);
        spFlowcon = (Spinner) findViewById(R.id.sp_flowcon);

        logListAdapter = new LogListAdapter(null);
        recy.setLayoutManager(new LinearLayoutManager(this));
        recy.setAdapter(logListAdapter);
        recy.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        serialPortFinder = new SerialPortFinder();
        serialHelper = new SerialHelper("dev/ttyS1", 115200) {
            @Override
            protected void onDataReceived(final ComBean comBean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton1) {
                            try {
                                Toast.makeText(getBaseContext(), new String(comBean.bRec, "UTF-8"), Toast.LENGTH_SHORT).show();
                                logListAdapter.addData(comBean.sRecTime + ":   " + new String(comBean.bRec, "UTF-8"));
                                if (logListAdapter.getData() != null && logListAdapter.getData().size() > 0) {
                                    recy.smoothScrollToPosition(logListAdapter.getData().size());
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getBaseContext(), ByteUtil.ByteArrToHex(comBean.bRec), Toast.LENGTH_SHORT).show();
                            logListAdapter.addData(comBean.sRecTime + ":   " + ByteUtil.ByteArrToHex(comBean.bRec));
                            if (logListAdapter.getData() != null && logListAdapter.getData().size() > 0) {
                                recy.smoothScrollToPosition(logListAdapter.getData().size());
                            }
                        }
                    }
                });
            }
        };

        final String[] ports = serialPortFinder.getAllDevicesPath();
        final String[] botes = new String[]{"0", "50", "75", "110", "134", "150", "200", "300", "600", "1200", "1800", "2400", "4800", "9600", "19200", "38400", "57600", "115200", "230400", "460800", "500000", "576000", "921600", "1000000", "1152000", "1500000", "2000000", "2500000", "3000000", "3500000", "4000000"};
        final String[] databits = new String[]{"8", "7", "6", "5"};
        final String[] paritys = new String[]{"NONE", "ODD", "EVEN"};
        final String[] stopbits = new String[]{"1", "2"};
        final String[] flowcons = new String[]{"NONE", "RTS/CTS", "XON/XOFF"};


        SpAdapter spAdapter = new SpAdapter(this);
        spAdapter.setDatas(ports);
        spSerial.setAdapter(spAdapter);

        spSerial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serialHelper.close();
                serialHelper.setPort(ports[position]);
                btOpen.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SpAdapter spAdapter2 = new SpAdapter(this);
        spAdapter2.setDatas(botes);
        spBote.setAdapter(spAdapter2);

        spBote.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serialHelper.close();
                serialHelper.setBaudRate(botes[position]);
                btOpen.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SpAdapter spAdapter3 = new SpAdapter(this);
        spAdapter3.setDatas(databits);
        spDatab.setAdapter(spAdapter3);

        spDatab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serialHelper.close();
                serialHelper.setDataBits(Integer.parseInt(databits[position]));
                btOpen.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SpAdapter spAdapter4 = new SpAdapter(this);
        spAdapter4.setDatas(paritys);
        spParity.setAdapter(spAdapter4);

        spParity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serialHelper.close();
                serialHelper.setParity(position);
                btOpen.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SpAdapter spAdapter5 = new SpAdapter(this);
        spAdapter5.setDatas(stopbits);
        spStopb.setAdapter(spAdapter5);

        spStopb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serialHelper.close();
                serialHelper.setStopBits(Integer.parseInt(stopbits[position]));
                btOpen.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SpAdapter spAdapter6 = new SpAdapter(this);
        spAdapter6.setDatas(flowcons);
        spFlowcon.setAdapter(spAdapter6);

        spFlowcon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serialHelper.close();
                serialHelper.setFlowCon(position);
                btOpen.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    serialHelper.open();
                    btOpen.setEnabled(false);
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "msg: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (SecurityException ex) {
                    Toast.makeText(MainActivity.this, "msg: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton1) {
                    if (edInput.getText().toString().length() > 0) {
                        if (serialHelper.isOpen()) {
                            serialHelper.sendTxt(edInput.getText().toString());
                        } else {
                            Toast.makeText(getBaseContext(), "串口没打开", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "先填数据吧", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (edInput.getText().toString().length() > 0) {
                        if (serialHelper.isOpen()) {
                            serialHelper.sendHex(edInput.getText().toString());
                        } else {
                            Toast.makeText(getBaseContext(), "串口没打开", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "先填数据吧", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clean:
                logListAdapter.clean(); //清空
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
