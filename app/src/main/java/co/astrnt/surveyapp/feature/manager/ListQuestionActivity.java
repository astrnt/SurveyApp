package co.astrnt.surveyapp.feature.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.astrnt.managersdk.core.MyObserver;
import co.astrnt.managersdk.dao.JobApiDao;
import co.astrnt.managersdk.dao.ListQuestionApiDao;
import co.astrnt.managersdk.dao.QuestionApiDao;
import co.astrnt.managersdk.repository.QuestionRepository;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;
import co.astrnt.surveyapp.feature.manager.adapter.QuestionAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ListQuestionActivity extends BaseActivity {

    private static final String EXT_DATA = "EXT_DATA";
    private QuestionRepository mQuestionRepository;
    private RecyclerView recyclerView;
    private TextView txtJobName;
    private FloatingActionButton fabAdd;
    private QuestionAdapter questionAdapter;
    private ProgressDialog progressDialog;

    private List<QuestionApiDao> listQuestion = new ArrayList<>();
    private JobApiDao jobApiDao;

    public static void start(Context context, JobApiDao jobApiDao) {
        Intent intent = new Intent(context, ListQuestionActivity.class);
        intent.putExtra(EXT_DATA, jobApiDao);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_question);

        getSupportActionBar().setTitle("List Question");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        txtJobName = findViewById(R.id.txt_job_name);
        fabAdd = findViewById(R.id.fab_add);

        mQuestionRepository = new QuestionRepository(getManagerApi());
        jobApiDao = getIntent().getParcelableExtra(EXT_DATA);

        questionAdapter = new QuestionAdapter(context, jobApiDao, listQuestion);

        txtJobName.setText(jobApiDao.getJob_name());

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(questionAdapter);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddQuestionActivity.start(context, jobApiDao);
            }
        });
        getData();
    }

    private void getData() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mQuestionRepository.getListQuestion(jobApiDao.getJob_identifier())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<ListQuestionApiDao>() {

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
                    public void onApiResultOk(ListQuestionApiDao apiDao) {
                        Timber.d(apiDao.getMessage());
                        listQuestion = apiDao.getQuestions();
                        questionAdapter.setData(listQuestion);
                    }
                });
    }

}
