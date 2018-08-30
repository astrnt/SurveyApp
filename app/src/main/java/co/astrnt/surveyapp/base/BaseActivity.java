package co.astrnt.surveyapp.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import co.astrnt.managersdk.ManagerSDK;
import co.astrnt.managersdk.core.ManagerApi;
import co.astrnt.qasdk.VideoSDK;
import co.astrnt.qasdk.core.AstronautApi;
import co.astrnt.qasdk.dao.InterviewApiDao;
import co.astrnt.surveyapp.AstronautApp;

public class BaseActivity extends AppCompatActivity {
    protected Context context = this;
    protected VideoSDK videoSDK;
    protected ManagerSDK managerSDK;
    protected InterviewApiDao interviewApiDao;

    public static AstronautApi getApi() {
        return AstronautApp.getApi();
    }

    public static ManagerApi getManagerApi() {
        return AstronautApp.getManagerApi();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoSDK = new VideoSDK();
        managerSDK = new ManagerSDK();
        interviewApiDao = videoSDK.getCurrentInterview();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
