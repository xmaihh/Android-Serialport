package com.ex.serialport;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ex.serialport.adapter.LogListAdapter;
import com.ex.serialport.adapter.SpAdapter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private TextView customBaudrate;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serialHelper.close();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recy = findViewById(R.id.recyclerView);
        spSerial = findViewById(R.id.sp_serial);
        edInput = findViewById(R.id.ed_input);
        btSend = findViewById(R.id.btn_send);
        spBote = findViewById(R.id.sp_baudrate);
        btOpen = findViewById(R.id.btn_open);

        radioGroup = findViewById(R.id.radioGroup);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);

        spDatab = findViewById(R.id.sp_databits);
        spParity = findViewById(R.id.sp_parity);
        spStopb = findViewById(R.id.sp_stopbits);
        spFlowcon = findViewById(R.id.sp_flowcon);
        customBaudrate = findViewById(R.id.tv_custom_baudrate);

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
                            logListAdapter.addData(comBean.sRecTime + " Rx:<==" + new String(comBean.bRec, StandardCharsets.UTF_8));
                            if (logListAdapter.getData() != null && logListAdapter.getData().size() > 0) {
                                recy.smoothScrollToPosition(logListAdapter.getData().size());
                            }
                        } else {
                            logListAdapter.addData(comBean.sRecTime + " Rx:<==" + ByteUtil.ByteArrToHex(comBean.bRec));
                            if (logListAdapter.getData() != null && logListAdapter.getData().size() > 0) {
                                recy.smoothScrollToPosition(logListAdapter.getData().size());
                            }
                        }
                    }
                });
            }
        };

        final String[] ports = serialPortFinder.getAllDevicesPath();
        final String[] botes = new String[]{"0", "50", "75", "110", "134", "150", "200", "300", "600", "1200", "1800", "2400", "4800", "9600", "19200", "38400", "57600", "115200", "230400", "460800", "500000", "576000", "921600", "1000000", "1152000", "1500000", "2000000", "2500000", "3000000", "3500000", "4000000", "CUSTOM"};
        final String[] databits = new String[]{"8", "7", "6", "5"};
        final String[] paritys = new String[]{"NONE", "ODD", "EVEN", "SPACE", "MARK"};
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
                if (position == botes.length - 1) {
                    showInputDialog();
                    return;
                }
                findViewById(R.id.tv_custom_baudrate).setVisibility(View.GONE);
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
                    Toast.makeText(MainActivity.this, getString(R.string.tips_cannot_be_opened, e.getMessage()), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (SecurityException se) {
                    Toast.makeText(MainActivity.this, getString(R.string.tips_cannot_be_opened, se.getMessage()), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss.SSS");
                if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton1) {
                    if (edInput.getText().toString().length() > 0) {
                        if (serialHelper.isOpen()) {
                            serialHelper.sendTxt(edInput.getText().toString());
                            logListAdapter.addData(sDateFormat.format(new Date()) + " Tx:==>" + edInput.getText().toString());
                            if (logListAdapter.getData() != null && logListAdapter.getData().size() > 0) {
                                recy.smoothScrollToPosition(logListAdapter.getData().size());
                            }
                        } else {
                            Toast.makeText(getBaseContext(), R.string.tips_serial_port_not_open, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), R.string.tips_please_enter_a_data, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (edInput.getText().toString().length() > 0) {
                        if (serialHelper.isOpen()) {
                            try {
                                Long.parseLong(edInput.getText().toString(), 16);
                            } catch (NumberFormatException e) {
                                Toast.makeText(getBaseContext(), R.string.tips_formatting_hex_error, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            serialHelper.sendHex(edInput.getText().toString());
                            logListAdapter.addData(sDateFormat.format(new Date()) + " Tx:==>" + edInput.getText().toString());
                            if (logListAdapter.getData() != null && logListAdapter.getData().size() > 0) {
                                recy.smoothScrollToPosition(logListAdapter.getData().size());
                            }
                        } else {
                            Toast.makeText(getBaseContext(), R.string.tips_serial_port_not_open, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), R.string.tips_please_enter_a_data, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.tips_please_enter_custom_baudrate);

        final EditText inputField = new EditText(this);
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        inputField.setFilters(new InputFilter[]{filter});
        builder.setView(inputField);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String userInput = inputField.getText().toString().trim();
                try {
                    int value = Integer.parseInt(userInput);
                    if (value >= 0 && value <= 4000000) {
                        customBaudrate.setVisibility(View.VISIBLE);
                        customBaudrate.setText(getString(R.string.title_custom_buardate, userInput));
                        serialHelper.close();
                        serialHelper.setBaudRate(userInput);
                        btOpen.setEnabled(true);
                    }
                } catch (NumberFormatException e) {
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_clean) {
            logListAdapter.clean();
        }
        return super.onOptionsItemSelected(item);
    }
}
