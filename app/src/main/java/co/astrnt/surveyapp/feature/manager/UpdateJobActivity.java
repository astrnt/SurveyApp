package co.astrnt.surveyapp.feature.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.astrnt.managersdk.core.MyObserver;
import co.astrnt.managersdk.dao.BaseApiDao;
import co.astrnt.managersdk.dao.JobApiDao;
import co.astrnt.managersdk.repository.JobRepository;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class UpdateJobActivity extends BaseActivity {

    private static final String EXT_DATA = "EXT_DATA";
    private JobRepository mJobRepository;
    private EditText inpJobTitle;
    private Button btnSubmit;
    private ProgressDialog progressDialog;

    private JobApiDao jobApiDao;
    private String jobTitle;

    public static void start(Context context, JobApiDao jobApiDao) {
        Intent intent = new Intent(context, UpdateJobActivity.class);
        intent.putExtra(EXT_DATA, jobApiDao);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Update Job");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setContentView(R.layout.activity_update_job);

        inpJobTitle = findViewById(R.id.inp_job_title);
        btnSubmit = findViewById(R.id.btn_submit);

        mJobRepository = new JobRepository(getManagerApi());

        jobApiDao = getIntent().getParcelableExtra(EXT_DATA);

        showInfo();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
            }
        });
    }

    private void showInfo() {
        inpJobTitle.setText(jobApiDao.getJob_name());
    }

    private void validateInput() {

        jobTitle = inpJobTitle.getText().toString();

        if (TextUtils.isEmpty(jobTitle)) {
            inpJobTitle.setError("Question Title is required");
            inpJobTitle.requestFocus();
            return;
        }

        createJob();
    }

    private void createJob() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mJobRepository.editJob(jobApiDao.getJob_identifier(), jobTitle)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<BaseApiDao>() {
                    @Override
                    public void onApiResultCompleted() {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onApiResultError(String message, String code) {
                        Timber.e(message);
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onApiResultOk(BaseApiDao apiDao) {
                        Timber.d(apiDao.getMessage());
                        Toast.makeText(context,
                                "Success Update Job",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
