package co.astrnt.surveyapp.feature.candidate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import co.astrnt.qasdk.AstrntSDK;
import co.astrnt.qasdk.core.MyObserver;
import co.astrnt.qasdk.dao.BaseApiDao;
import co.astrnt.qasdk.repository.InterviewRepository;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;
import co.astrnt.surveyapp.model.DeviceErrorDao;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class CheckingDeviceActivity extends BaseActivity {

    private InterviewRepository dataManager;
    private boolean isNetworkOk = true;
    private boolean isMemoryOk = true;
    private boolean isSoundOk = true;
    private boolean isCameraOk = true;
    private int permissionCounter = 0;

    private ProgressDialog progressDialog;
    private AstrntSDK astrntSDK;

    public static void start(Context context) {
        Intent intent = new Intent(context, CheckingDeviceActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking_device);

        astrntSDK = new AstrntSDK();
        dataManager = new InterviewRepository(getApi());

        pingNetwork();
        checkingAvailableStorage();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkingPermission();
                }
            }, 3000);
        } else {
            permissionCounter = 4;
            moveToNext();
        }
    }

    public void showNetworkOk(boolean isNetworkOk) {
        this.isNetworkOk = isNetworkOk;
        permissionCounter++;
        moveToNext();
    }

    public void showNext() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                VideoInstructionActivity.start(context);
                finish();
            }
        }, 3000);
    }

    private void checkingAvailableStorage() {
        isMemoryOk = astrntSDK.getAvailableMemory() > 100 + (astrntSDK.getTotalQuestion() * 5);
    }

    @SuppressLint("CheckResult")
    private void checkingPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);

        rxPermissions
                .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO)
                .subscribe(new Observer<Permission>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Permission permission) {
                        // will emit 1 Permission object
                        if (permission.granted) {
                            // All permissions are granted !
                            permissionChecking(permission, true);
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // At least one denied permission without ask never again

                            Toast.makeText(context, permission.name + " is not Granted", Toast.LENGTH_SHORT).show();
                            permissionChecking(permission, false);
                        } else {
                            // At least one denied permission with ask never again
                            // Need to go to the settings
                            permissionChecking(permission, false);
                            Toast.makeText(context, permission.name + " is not Granted and never Ask Again, Please go to Settings", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        permissionCounter = 4;
                        moveToNext();
                    }
                });
    }

    private void permissionChecking(Permission permission, boolean granted) {
        switch (permission.name) {
            case Manifest.permission.CAMERA:
                isCameraOk = granted;
                break;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                isMemoryOk = granted;
                break;
            case Manifest.permission.RECORD_AUDIO:
                isSoundOk = granted;
                break;
            default:
                break;
        }
    }

    private void moveToNext() {
        if (permissionCounter < 4) {
            return;
        }
        if (isNetworkOk && isMemoryOk && isCameraOk && isSoundOk) {
            showNext();
        } else {
            Timber.d("checking device failed");
            DeviceErrorDao deviceErrorDao = new DeviceErrorDao(isMemoryOk, isCameraOk, isSoundOk, isNetworkOk, DeviceErrorDao.TYPE_DEVICE);
            CheckingDeviceErrorActivity.start(context, deviceErrorDao);
            finish();
        }
    }

    public void pingNetwork() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final long startTime = System.currentTimeMillis();

        dataManager.pingNetwork()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<BaseApiDao>() {

                    @Override
                    public void onApiResultCompleted() {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onApiResultError(String message, String code) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onApiResultOk(BaseApiDao baseApiDao) {
                        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                        Timber.d("ping time : %s", elapsedTime);

                        isNetworkOk = elapsedTime < 5;
                        showNetworkOk(isNetworkOk);
                    }
                });
    }
}
