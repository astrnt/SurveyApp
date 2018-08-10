package co.astrnt.surveyapp;

import android.app.Application;

import co.astrnt.managersdk.ManagerSDK;
import co.astrnt.managersdk.core.ManagerApi;
import co.astrnt.qasdk.AstrntSDK;
import co.astrnt.qasdk.core.AstronautApi;

public class AstronautApp extends Application {

    private static AstrntSDK astrntSDK;
    private static ManagerSDK managerSDK;

    public static AstronautApi getApi() {
        return astrntSDK.getApi();
    }

    public static ManagerApi getManagerApi() {
        return managerSDK.getApi();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUpSDK();
    }

    private void setUpSDK() {
        if (astrntSDK == null) {
            astrntSDK = new AstrntSDK(this, BuildConfig.API_URL, BuildConfig.DEBUG, BuildConfig.APPLICATION_ID);
        }
        if (managerSDK == null) {
            managerSDK = new ManagerSDK(this, BuildConfig.API_URL, BuildConfig.DEBUG, BuildConfig.API_KEY);
        }
    }
}
