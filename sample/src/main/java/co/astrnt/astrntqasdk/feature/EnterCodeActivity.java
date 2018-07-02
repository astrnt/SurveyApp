package co.astrnt.astrntqasdk.feature;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.astrnt.astrntqasdk.BuildConfig;
import co.astrnt.astrntqasdk.R;
import co.astrnt.astrntqasdk.base.BaseActivity;
import co.astrnt.qasdk.core.InterviewObserver;
import co.astrnt.qasdk.dao.InterviewApiDao;
import co.astrnt.qasdk.repository.InterviewRepository;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class EnterCodeActivity extends BaseActivity {

    private InterviewRepository mInterviewRepository;
    private EditText inpCode;
    private Button btnSubmit;
    private ProgressDialog progressDialog;

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

        mInterviewRepository = new InterviewRepository(getApi());

        astrntSDK.clearDb();
        if (BuildConfig.DEBUG) {
            inpCode.setText("video");
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = inpCode.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    inpCode.setError("Code still empty");
                    inpCode.setFocusable(true);
                    return;
                }
                enterCode(code);
            }
        });
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
