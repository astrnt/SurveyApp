package co.astrnt.surveyapp.feature.candidate;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;
import co.astrnt.surveyapp.model.DeviceErrorDao;

public class CheckingDeviceErrorActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout lyStorage;
    private RelativeLayout lyCamera;
    private RelativeLayout lyMicrophone;
    private RelativeLayout lyNetwork;
    private TextView txtSettingsCamera, txtSettingsMicrophone;

    private DeviceErrorDao deviceErrorDao;

    public static void start(Context context, DeviceErrorDao deviceErrorDao) {
        Intent intent = new Intent(context, CheckingDeviceErrorActivity.class);
        intent.putExtra(DeviceErrorDao.class.getName(), deviceErrorDao);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking_device_error);

        lyStorage = findViewById(R.id.ly_storage);
        lyCamera = findViewById(R.id.ly_camera);
        lyMicrophone = findViewById(R.id.ly_microphone);
        lyNetwork = findViewById(R.id.ly_network);
        txtSettingsCamera = findViewById(R.id.txt_camera_settings);
        txtSettingsMicrophone = findViewById(R.id.txt_microphone_settings);

        deviceErrorDao = getIntent().getParcelableExtra(DeviceErrorDao.class.getName());

        txtSettingsCamera.setOnClickListener(this);
        txtSettingsMicrophone.setOnClickListener(this);

        if (deviceErrorDao.isMemoryOk()) {
            lyStorage.setVisibility(View.GONE);
        } else {
            lyStorage.setVisibility(View.VISIBLE);
        }

        if (deviceErrorDao.isCameraOk()) {
            lyCamera.setVisibility(View.GONE);
        } else {
            lyCamera.setVisibility(View.VISIBLE);
        }

        if (deviceErrorDao.isSoundOk()) {
            lyMicrophone.setVisibility(View.GONE);
        } else {
            lyMicrophone.setVisibility(View.VISIBLE);
        }

        if (deviceErrorDao.isNetworkOk()) {
            lyNetwork.setVisibility(View.GONE);
        } else {
            lyNetwork.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_camera_settings:
            case R.id.txt_microphone_settings:
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btn_submit:
                if (deviceErrorDao.getType() == DeviceErrorDao.TYPE_DEVICE) {
                    CheckingDeviceActivity.start(context);
                } else {
                    CheckingNetworkActivity.start(context);
                }
                finish();
                break;
        }
    }
}
