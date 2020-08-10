package com.berrontech.huali.alarmcenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Create by levent at 2020/8/8 17:44
 * MainActivity
 * Main Activity
 *
 * @author levent
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        final Button btnTcpMaster = ViewUtils.find(this, R.id.btnTcpMaster);
        final Button btnSerialMaster = ViewUtils.find(this, R.id.btnSerialMaster);
        btnTcpMaster.setOnClickListener(this);
        btnSerialMaster.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTcpMaster:
                startActivity(TcpMasterTestActivity.class);
                break;
            case R.id.btnSerialMaster:
                break;
            default:
                //Do Nothing
        }
    }

    private void startActivity(Class<? extends Activity> activityClass) {
        final Intent intent = new Intent();
        intent.setClass(this, activityClass);
        startActivity(intent);
    }
}
