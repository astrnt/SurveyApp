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
import co.astrnt.managersdk.dao.QuestionApiDao;
import co.astrnt.managersdk.dao.ResponseVideoApiDao;
import co.astrnt.managersdk.dao.VideoApiDao;
import co.astrnt.managersdk.repository.CandidateRepository;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;
import co.astrnt.surveyapp.feature.manager.adapter.VideoAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ListVideoActivity extends BaseActivity {

    private static final String EXT_JOB_DATA = "EXT_JOB_DATA";
    private static final String EXT_CANDIDATE_ID = "CANDIDATE_ID";
    private static final String EXT_QUESTION_DATA = "QUESTION_ID";
    private CandidateRepository mCandidateRepository;
    private RecyclerView recyclerView;
    private TextView txtJobName;
    private FloatingActionButton fabAdd;
    private VideoAdapter videoAdapter;
    private ProgressDialog progressDialog;

    private List<VideoApiDao> listVideos = new ArrayList<>();
    private JobApiDao jobApiDao;
    private QuestionApiDao questionApiDao;
    private String candidateId;

    public static void start(Context context, JobApiDao jobApiDao, String candidateId) {
        Intent intent = new Intent(context, ListVideoActivity.class);
        intent.putExtra(EXT_JOB_DATA, jobApiDao);
        intent.putExtra(EXT_CANDIDATE_ID, candidateId);
        context.startActivity(intent);
    }

    public static void startFromQuestion(Context context, JobApiDao jobApiDao, QuestionApiDao question) {
        Intent intent = new Intent(context, ListVideoActivity.class);
        intent.putExtra(EXT_JOB_DATA, jobApiDao);
        intent.putExtra(EXT_QUESTION_DATA, question);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_video_candidate);

        getSupportActionBar().setTitle("List Video");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.recycler_view);
        txtJobName = findViewById(R.id.txt_job_name);
        fabAdd = findViewById(R.id.fab_add);

        mCandidateRepository = new CandidateRepository(getManagerApi());

        videoAdapter = new VideoAdapter(context, listVideos);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(videoAdapter);

        jobApiDao = getIntent().getParcelableExtra(EXT_JOB_DATA);
        candidateId = getIntent().getStringExtra(EXT_CANDIDATE_ID);
        if (candidateId == null) {
            questionApiDao = getIntent().getParcelableExtra(EXT_QUESTION_DATA);
        } else {
            questionApiDao = new QuestionApiDao();
        }

        txtJobName.setText(jobApiDao.getJob_name());

        fabAdd.setVisibility(View.GONE);
        getData();
    }

    private void getData() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mCandidateRepository.getListVideos(jobApiDao.getJob_identifier(), candidateId, questionApiDao.getQuestion_identifier())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<ResponseVideoApiDao>() {

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
                    public void onApiResultOk(ResponseVideoApiDao apiDao) {
                        Timber.d(apiDao.getMessage());
                        listVideos = apiDao.getVideos();
                        videoAdapter.setData(listVideos);
                    }
                });
    }

}
