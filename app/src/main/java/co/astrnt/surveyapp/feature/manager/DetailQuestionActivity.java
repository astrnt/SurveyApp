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
import co.astrnt.managersdk.dao.QuestionApiDao;
import co.astrnt.managersdk.repository.QuestionRepository;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class DetailQuestionActivity extends BaseActivity implements View.OnClickListener {

    private static final String EXT_DATA_JOB = "EXT_DATA_JOB";
    private static final String EXT_DATA = "EXT_DATA";

    private QuestionRepository mQuestionRepository;
    private ProgressDialog progressDialog;

    private TextView txtTitle, txtTakesCount;
    private Button btnSeeVideos, btnDeleteJob, btnUpdateJob;
    private QuestionApiDao questionApiDao;
    private JobApiDao jobApiDao;

    public static void start(Context context, JobApiDao jobApiDao, QuestionApiDao questionApiDao) {
        Intent intent = new Intent(context, DetailQuestionActivity.class);
        intent.putExtra(EXT_DATA, questionApiDao);
        intent.putExtra(EXT_DATA_JOB, jobApiDao);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Detail Question");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setContentView(R.layout.activity_detail_question);

        txtTitle = findViewById(R.id.txt_question_title);
        txtTakesCount = findViewById(R.id.txt_takes_and_max_time);

        btnSeeVideos = findViewById(R.id.btn_see_videos);
        btnUpdateJob = findViewById(R.id.btn_update);
        btnDeleteJob = findViewById(R.id.btn_delete);

        questionApiDao = getIntent().getParcelableExtra(EXT_DATA);
        jobApiDao = getIntent().getParcelableExtra(EXT_DATA_JOB);
        mQuestionRepository = new QuestionRepository(getManagerApi());

        showInfo();

        btnSeeVideos.setOnClickListener(this);
        btnUpdateJob.setOnClickListener(this);
        btnDeleteJob.setOnClickListener(this);
    }

    private void showInfo() {
        txtTitle.setText(questionApiDao.getTitle());
        txtTakesCount.setText(questionApiDao.getMaxTime() + "s - " + questionApiDao.getTakesCount() + "x");
    }

    private void deleteQuestion() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mQuestionRepository.deleteQuestion(jobApiDao.getJob_identifier(), questionApiDao.getQuestion_identifier())
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_see_videos:
                ListVideoActivity.startFromQuestion(context, jobApiDao, questionApiDao);
                break;
            case R.id.btn_update:
                UpdateQuestionActivity.start(context, jobApiDao, questionApiDao);
                break;
            case R.id.btn_delete:
                deleteQuestion();
                break;
            default:
                break;
        }
    }
}
