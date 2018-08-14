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
import co.astrnt.managersdk.dao.QuestionApiDao;
import co.astrnt.managersdk.repository.QuestionRepository;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class UpdateQuestionActivity extends BaseActivity {

    private static final String EXT_DATA = "EXT_DATA";
    private static final String EXT_JOB_DATA = "EXT_JOB_DATA";
    private QuestionRepository mQuestionRepository;
    private EditText inpQuestionTitle, inpTakesCount;
    private Button btnSubmit;
    private ProgressDialog progressDialog;

    private JobApiDao jobApiDao;
    private QuestionApiDao questionApiDao;
    private String questionTitle;
    private int takesCount;

    public static void start(Context context, JobApiDao jobApiDao, QuestionApiDao questionApiDao) {
        Intent intent = new Intent(context, UpdateQuestionActivity.class);
        intent.putExtra(EXT_JOB_DATA, jobApiDao);
        intent.putExtra(EXT_DATA, questionApiDao);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Update Question");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setContentView(R.layout.activity_add_question);

        inpQuestionTitle = findViewById(R.id.inp_question_title);
        inpTakesCount = findViewById(R.id.inp_takes_count);
        btnSubmit = findViewById(R.id.btn_submit);

        mQuestionRepository = new QuestionRepository(getManagerApi());

        jobApiDao = getIntent().getParcelableExtra(EXT_JOB_DATA);
        questionApiDao = getIntent().getParcelableExtra(EXT_DATA);

        showInfo();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
            }
        });
    }

    private void showInfo() {
        inpQuestionTitle.setText(questionApiDao.getTitle());
        inpTakesCount.setText(String.valueOf(questionApiDao.getTakesCount()));
    }

    private void validateInput() {

        questionTitle = inpQuestionTitle.getText().toString();
        String takesCountStr = inpTakesCount.getText().toString();

        if (TextUtils.isEmpty(questionTitle)) {
            inpQuestionTitle.setError("Question Title is required");
            inpQuestionTitle.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(takesCountStr)) {
            inpTakesCount.setError("Takes Count is required");
            inpTakesCount.requestFocus();
            return;
        }

        takesCount = Integer.valueOf(takesCountStr);

        createJob();
    }

    private void createJob() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mQuestionRepository.editQuestion(jobApiDao.getJob_identifier(), questionApiDao.getQuestion_identifier(), questionTitle, takesCount)
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
                                "Success Edit Question",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
