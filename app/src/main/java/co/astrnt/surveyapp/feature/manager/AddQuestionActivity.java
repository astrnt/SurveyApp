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
import co.astrnt.managersdk.dao.AddQuestionApiDao;
import co.astrnt.managersdk.dao.JobApiDao;
import co.astrnt.managersdk.repository.QuestionRepository;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AddQuestionActivity extends BaseActivity {

    private static final String EXT_DATA = "EXT_DATA";
    private QuestionRepository mQuestionRepository;
    private EditText inpQuestionTitle, inpTakesCount;
    private Button btnSubmit;
    private ProgressDialog progressDialog;

    private JobApiDao jobApiDao;
    private String questionTitle;
    private int takesCount;

    public static void start(Context context, JobApiDao jobApiDao) {
        Intent intent = new Intent(context, AddQuestionActivity.class);
        intent.putExtra(EXT_DATA, jobApiDao);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Add Question");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setContentView(R.layout.activity_add_question);

        inpQuestionTitle = findViewById(R.id.inp_question_title);
        inpTakesCount = findViewById(R.id.inp_takes_count);
        btnSubmit = findViewById(R.id.btn_submit);

        mQuestionRepository = new QuestionRepository(getManagerApi());

        jobApiDao = getIntent().getParcelableExtra(EXT_DATA);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
            }
        });
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

        mQuestionRepository.addQuestion(jobApiDao.getJob_identifier(), questionTitle, takesCount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<AddQuestionApiDao>() {
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
                    public void onApiResultOk(AddQuestionApiDao apiDao) {
                        Timber.d(apiDao.getMessage());
                        Toast.makeText(context,
                                "Success Add Question, Question id : " + apiDao.getQuestion_identifier(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
