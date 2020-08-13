package com.berrontech.huali.alarmcenter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.berrontech.huali.modbus.AsyncModbusMaster;
import com.berrontech.huali.modbus.ModbusCallback;
import com.berrontech.huali.modbus.ModbusFactory;
import com.berrontech.huali.modbus.ModbusMaster;
import com.berrontech.huali.modbus.exception.ModbusTransportException;
import com.berrontech.huali.modbus.msg.ModbusRequest;
import com.berrontech.huali.modbus.msg.ModbusResponse;
import com.berrontech.huali.modbus.msg.ReadCoilsRequest;
import com.berrontech.huali.modbus.msg.ReadHoldingRegistersRequest;
import com.berrontech.huali.modbus.msg.ReadHoldingRegistersResponse;
import com.berrontech.huali.modbus.serial.SerialPortWrapper;
import com.berrontech.huali.serialport.SimpleSerialPortWrapper;

import java.io.File;

import android_serialport_api.SerialPortFinder;

public class SerialPortMasterTestActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, ModbusCallback<Void> {
    private static final String TAG = SerialPortMasterTestActivity.class.getSimpleName();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Spinner spSerialPort;
    private EditText etVersion;
    private AsyncModbusMaster modbusMaster;
    private String serialPortDevicePath;
    private String[] devicePathList;
    private TextView tvSerialInfo;
    private boolean initialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_port_master_test);
        initView();
        scanSerialPort();
    }

    private void scanSerialPort() {
        devicePathList = SerialPortFinder.INSTANCE.getAllDevicesPath();
        final SpinnerAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, devicePathList);
        spSerialPort.setAdapter(adapter);
        spSerialPort.setOnItemSelectedListener(this);
        tvSerialInfo = ViewUtils.find(this, R.id.tvSerialInfo);
        etVersion = ViewUtils.find(this, R.id.etVersion);
    }

    private void initView() {
        this.spSerialPort = ViewUtils.find(this, R.id.spSerialPort);
        initBtnListener(R.id.btnInit);
        initBtnListener(R.id.btnReadCoils);
        initBtnListener(R.id.btnReadVersion);
    }

    private void initBtnListener(int viewId) {
        final Button btn = ViewUtils.find(this, viewId);
        if (btn != null) {
            btn.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnInit:
                initSerialMaster();
                break;
            case R.id.btnReadCoils:
                readCoils();
            case R.id.btnReadVersion:
                readVersion();
                break;
            default:
                //Do Nothing
        }
    }

    private void readVersion() {
        modbusMaster.runOnInitialized(() -> {
            try {
                final ModbusRequest request = new ReadHoldingRegistersRequest(1, 8, 2);
                final ModbusResponse response = modbusMaster.sendSync(request);
                final byte[] data = ((ReadHoldingRegistersResponse) response).getData();
                showVersion(data);
            } catch (ModbusTransportException e) {
                Log.d(TAG, String.format("ERROR: %s/%s", e.getClass().getSimpleName(), e.getMessage()));
            }
        });
    }

    private void showVersion(byte[] data) {
        final StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            final String iStr = Integer.toHexString(b);
            sb.append(iStr).append(",");
        }
        handler.post(() -> etVersion.setText(sb));
    }

    private void initSerialMaster() {
        if (modbusMaster != null) {
            modbusMaster.runOnInitialized(modbusMaster::destroyQuiet);
        }
        final SerialPortWrapper wrapper = buildSerialPortWrapper();
        if (wrapper == null) {
            return;
        }
        final ModbusMaster master = ModbusFactory.INSTANCE.createRtuMaster(wrapper);
        this.modbusMaster = new AsyncModbusMaster(master);
        modbusMaster.init(this);
        initialized = true;
        refreshInitializedStatus();
    }

    private SerialPortWrapper buildSerialPortWrapper() {
        if (serialPortDevicePath == null) {
            showToastSync("串口未选择");
            return null;
        }
        final File deviceFile = new File(serialPortDevicePath);
        return new SimpleSerialPortWrapper(deviceFile, 19200);
    }

    private void readCoils() {
        modbusMaster.runOnInitialized(() -> {
            final ModbusRequest request;
            try {
                request = new ReadCoilsRequest(1, 1, 1);
                modbusMaster.sendSync(request);
            } catch (ModbusTransportException e) {
                Log.d(TAG, String.format("ERROR:%s:[%s]", e.getClass().getSimpleName(), e.getMessage()));
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        serialPortDevicePath = devicePathList[position];
        initialized = false;
        refreshInitializedStatus();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        serialPortDevicePath = null;
        initialized = false;
        refreshInitializedStatus();
    }

    @Override
    public void onSuccess(Void result) {
        showToast("Success");
    }

    @Override
    public void onFail(Throwable err, Void result) {
        showToast("Fail:" + err.getMessage());
    }

    @Override
    public void onComplete(Throwable err, Void result) {
        showToast("Complete");
    }

    private void showToast(final String str) {
        handler.post(() -> showToastSync(str));
    }

    private void showToastSync(String str) {
        Toast.makeText(SerialPortMasterTestActivity.this, str, 2 * 1000).show();
    }

    private void refreshInitializedStatus() {
        final String textInfo = String.format("%s/19200", serialPortDevicePath);
        tvSerialInfo.setText(textInfo);
        final int colorId = initialized ? R.color.primaryColor : R.color.waringColor;
        int color = getResources().getColor(colorId);
        tvSerialInfo.setTextColor(color);
    }
}
