package co.astrnt.surveyapp.feature.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class DetailJobActivity extends BaseActivity implements View.OnClickListener {

    private static final String EXT_DATA = "EXT_DATA";

    private co.astrnt.managersdk.repository.JobRepository JobRepository;
    private ProgressDialog progressDialog;

    private TextView txtTitle, txtJobType, txtDescription, txtTotalCandidate,
            txtRequirements, txtResponsibility, txtDeadline, txtOpenCode;
    private Button btnSeeCandidates, btnSeeQuestions, btnDeleteJob, btnUpdateJob;
    private JobApiDao jobApiDao;

    public static void start(Context context, JobApiDao jobApiDao) {
        Intent intent = new Intent(context, DetailJobActivity.class);
        intent.putExtra(EXT_DATA, jobApiDao);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Detail Job");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setContentView(R.layout.activity_detail_job);

        txtTitle = findViewById(R.id.txt_job_title);
        txtJobType = findViewById(R.id.txt_job_type);
        txtDescription = findViewById(R.id.txt_job_description);
        txtRequirements = findViewById(R.id.txt_job_requirement);
        txtResponsibility = findViewById(R.id.txt_job_responsibility);
        txtTotalCandidate = findViewById(R.id.txt_total_candidate);
        txtDeadline = findViewById(R.id.txt_job_deadline);
        txtOpenCode = findViewById(R.id.txt_job_open_code);

        btnSeeCandidates = findViewById(R.id.btn_see_candidates);
        btnSeeQuestions = findViewById(R.id.btn_see_question);
        btnUpdateJob = findViewById(R.id.btn_update);
        btnDeleteJob = findViewById(R.id.btn_delete);

        jobApiDao = getIntent().getParcelableExtra(EXT_DATA);
        JobRepository = new JobRepository(getManagerApi());

        showInfo();

        btnSeeCandidates.setOnClickListener(this);
        btnSeeQuestions.setOnClickListener(this);
        btnUpdateJob.setOnClickListener(this);
        btnDeleteJob.setOnClickListener(this);
        txtOpenCode.setOnClickListener(this);
    }

    private void showInfo() {
        txtTitle.setText(jobApiDao.getJob_name());
        txtJobType.setText(jobApiDao.getJob_type() + " - " + jobApiDao.getLocations());
        txtDescription.setText("Description : " + jobApiDao.getDescription());
        txtRequirements.setText("Requirement : " + jobApiDao.getRequirement());
        txtResponsibility.setText("Responsibility : " + jobApiDao.getResponsibility());
        txtDeadline.setText("Deadline : " + jobApiDao.getDeadline());
        txtTotalCandidate.setText("Total Candidates : " + jobApiDao.getTotal_candidate());
        txtOpenCode.setText(jobApiDao.getOpen_code());
    }

    private void deleteJob() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        JobRepository.deleteJob(jobApiDao.getJob_identifier())
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
                        finish();
                    }
                });
    }

    private void shareJob() {
        String shareBody = "Our company is open the job. Download Astronaut App and use this code : " + jobApiDao.getOpen_code();
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share Using"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                UpdateJobActivity.start(context, jobApiDao);
                break;
            case R.id.btn_delete:
                deleteJob();
                break;
            case R.id.txt_job_open_code:
                shareJob();
                break;
            case R.id.btn_see_candidates:
                ListCandidateActivity.start(context, jobApiDao);
                break;
            case R.id.btn_see_question:
                ListQuestionActivity.start(context, jobApiDao);
                break;
        }
    }
}
