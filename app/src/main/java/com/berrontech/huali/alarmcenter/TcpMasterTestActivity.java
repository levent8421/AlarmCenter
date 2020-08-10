package com.berrontech.huali.alarmcenter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.berrontech.huali.modbus.AsyncModbusMaster;
import com.berrontech.huali.modbus.ModbusCallback;
import com.berrontech.huali.modbus.ModbusFactory;
import com.berrontech.huali.modbus.ModbusMaster;
import com.berrontech.huali.modbus.exception.ModbusTransportException;
import com.berrontech.huali.modbus.ip.IpParameters;
import com.berrontech.huali.modbus.msg.ModbusRequest;
import com.berrontech.huali.modbus.msg.ModbusResponse;
import com.berrontech.huali.modbus.msg.ReadCoilsRequest;

public class TcpMasterTestActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = TcpMasterTestActivity.class.getSimpleName();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private EditText etHost;
    private EditText etPort;
    private AsyncModbusMaster modbusMaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp_master_test);
        initView();
    }

    private void initView() {
        etHost = ViewUtils.find(this, R.id.etHost);
        etPort = ViewUtils.find(this, R.id.etPort);
        final Button btnInit = ViewUtils.find(this, R.id.btnInit);
        btnInit.setOnClickListener(this);
        final Button btnReadCoils = ViewUtils.find(this, R.id.btnReadCoils);
        btnReadCoils.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnInit:
                this.initTcpMaster();
                break;
            case R.id.btnReadCoils:
                this.readCoils();
                break;
            default:
                //Do Nothing
        }
    }

    private void readCoils() {
        if (modbusMaster == null) {
            return;
        }
        final ModbusRequest request;
        try {
            request = new ReadCoilsRequest(1, 0, 8);
        } catch (ModbusTransportException e) {
            throw new RuntimeException(e);
        }
        modbusMaster.runOnInitialized(new Runnable() {
            @Override
            public void run() {
                try {
                    final ModbusResponse response = modbusMaster.sendSync(request);
                    Log.d(TAG, response.getExceptionMessage());
                } catch (ModbusTransportException e) {
                    e.printStackTrace();
                    showToast("Send error = " + e.getMessage());
                }
            }
        });
    }

    private void initTcpMaster() {
        if (modbusMaster != null) {
            modbusMaster.runOnInitialized(new Runnable() {
                @Override
                public void run() {
                    modbusMaster.destroyQuiet();
                }
            });
        }
        modbusMaster = null;
        final IpParameters param = new IpParameters();
        final String host = etHost.getText().toString();
        final String portStr = etPort.getText().toString();
        param.setHost(host);
        param.setPort(Integer.parseInt(portStr));

        final ModbusMaster master = ModbusFactory.INSTANCE.createTcpMaster(param, true);
        this.modbusMaster = new AsyncModbusMaster(master);
        modbusMaster.init(new ModbusCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(TAG, "SUCCESS");
                showToast("Success");
            }

            @Override
            public void onFail(Throwable err, Void result) {
                Log.d(TAG, "FAIL");
                err.printStackTrace();
                showToast("Fail,err=" + err.getClass().getSimpleName());
            }

            @Override
            public void onComplete(Throwable err, Void result) {
                Log.d(TAG, "COMPLETE");
            }
        });
    }

    private void showToast(final String str) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TcpMasterTestActivity.this, str, 2 * 1000).show();
            }
        });
    }
}
