package co.astrnt.surveyapp.feature.candidate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import co.astrnt.qasdk.core.InterviewObserver;
import co.astrnt.qasdk.dao.InterviewApiDao;
import co.astrnt.qasdk.repository.InterviewRepository;
import co.astrnt.surveyapp.BuildConfig;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;
import co.astrnt.surveyapp.feature.manager.LoginActivity;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EnterCodeActivity extends BaseActivity implements View.OnClickListener {

    private InterviewRepository mInterviewRepository;
    private AppCompatEditText inpCode;
    private Button btnSubmit, btnLogin;
    private ProgressDialog progressDialog;

    private boolean isNetworkOk = true;
    private boolean isMemoryOk = true;
    private boolean isSoundOk = true;
    private boolean isCameraOk = true;
    private int permissionCounter = 0;

    public static void start(Context context) {
        Intent intent = new Intent(context, EnterCodeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code);

        context = this;

        inpCode = findViewById(R.id.inp_code);
        btnSubmit = findViewById(R.id.btn_submit);
        btnLogin = findViewById(R.id.btn_login);

        mInterviewRepository = new InterviewRepository(getApi());

        videoSDK.clearDb();
        if (BuildConfig.DEBUG) {
            inpCode.setText("upmVqCsC");
        }

        checkingPermission();

        btnSubmit.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @SuppressLint("CheckResult")
    private void checkingPermission() {
        checkingAvailableStorage();

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
                    }
                });
    }

    private void permissionChecking(Permission permission, boolean granted) {
        permissionCounter++;
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

    private void checkingAvailableStorage() {
        isMemoryOk = videoSDK.getAvailableMemory() > 100 + (videoSDK.getTotalQuestion() * 5);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                validateInput();
                break;
            case R.id.btn_login:
                moveToLogin();
                break;
        }
    }

    private void moveToLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    private void validateInput() {
        String code = inpCode.getText().toString();
        if (TextUtils.isEmpty(code)) {
            inpCode.setError("Code still empty");
            inpCode.setFocusable(true);
            return;
        }
        enterCode(code);
    }

    private void enterCode(final String code) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mInterviewRepository.enterCode(code, BuildConfig.SDK_VERSION)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new InterviewObserver() {

                    @Override
                    public void onApiResultCompleted() {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onApiResultError(String message, String code) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNeedToRegister(InterviewApiDao interview) {
                        interview.setTemp_code(code);
                        astrntSDK.saveInterview(interview, "", interview.getTemp_code());
                        Toast.makeText(context, "Need Register", Toast.LENGTH_SHORT).show();
                        RegisterActivity.start(context);
                        finish();
                    }

                    @Override
                    public void onInterviewType(InterviewApiDao interview) {
                        Toast.makeText(context, "Interview", Toast.LENGTH_SHORT).show();
                        VideoInfoActivity.start(context);
                        finish();
                    }

                    @Override
                    public void onTestType(InterviewApiDao interview) {
                        Toast.makeText(context, "Test MCQ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSectionType(InterviewApiDao interview) {
                        Toast.makeText(context, "Section", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
