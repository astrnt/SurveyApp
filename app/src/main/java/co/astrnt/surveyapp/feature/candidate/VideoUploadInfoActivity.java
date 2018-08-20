package co.astrnt.surveyapp.feature.candidate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.astrnt.qasdk.AstrntSDK;
import co.astrnt.qasdk.core.MyObserver;
import co.astrnt.qasdk.dao.BaseApiDao;
import co.astrnt.qasdk.dao.QuestionApiDao;
import co.astrnt.qasdk.repository.InterviewRepository;
import co.astrnt.qasdk.type.UploadStatusType;
import co.astrnt.qasdk.videocompressor.services.VideoCompressService;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;
import co.astrnt.surveyapp.feature.candidate.adapter.VideoUploadInfoAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class VideoUploadInfoActivity extends BaseActivity implements UploadStatusDelegate {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private int counter;
    private List<QuestionApiDao> questionApiDaoList = new ArrayList<>();
    private QuestionApiDao currentQuestion;
    private boolean menuShow = false;
    private int totalQuestion;

    private AstrntSDK astrntSDK;
    private InterviewRepository interviewRepository;
    private VideoUploadInfoAdapter adapter;
    private ProgressDialog progressDialog;

    public static void start(Context context) {
        Intent intent = new Intent(context, VideoUploadInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload_info);

        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh);
        recyclerView = findViewById(R.id.recycler_view_upload_info);

        astrntSDK = new AstrntSDK();
        interviewRepository = new InterviewRepository(getApi());

        totalQuestion = astrntSDK.getTotalQuestion();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetData();
            }
        });

        adapter = new VideoUploadInfoAdapter(context, questionApiDaoList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        updateAdapter();

        doUploadVideo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_upload_info, menu);
        menu.findItem(R.id.action_done).setVisible(menuShow);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_done) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateAdapter() {
        interviewApiDao = astrntSDK.getCurrentInterview();
        questionApiDaoList = interviewApiDao.getQuestions();
        adapter.setData(questionApiDaoList);

        swipeRefreshLayout.setRefreshing(false);
    }

    public void doUploadVideo() {

        if (counter == totalQuestion) {
            callFinish();
            return;
        }

        try {
            currentQuestion = questionApiDaoList.get(counter);

            if (currentQuestion.getUploadStatus().equals(UploadStatusType.PENDING)) {
                VideoCompressService.start(context, currentQuestion.getVideoPath(), currentQuestion.getId());
                return;
            }

            if (currentQuestion.getUploadStatus().equals(UploadStatusType.NOT_ANSWER) ||
                    currentQuestion.getUploadStatus().equals(UploadStatusType.UPLOADED)) {
                counter++;
                updateAdapter();
                doUploadVideo();
                return;
            }

            astrntSDK.markUploading(currentQuestion);

            UploadNotificationConfig notificationConfig = new UploadNotificationConfig();
            notificationConfig.setRingToneEnabled(false);

            String uploadId = new MultipartUploadRequest(context, astrntSDK.getApiUrl() + "video/upload")
                    .addHeader("token", interviewApiDao.getToken())
                    .addParameter("interview_code", interviewApiDao.getInterviewCode())
                    .addParameter("candidate_id", String.valueOf(interviewApiDao.getCandidate().getId()))
                    .addParameter("company_id", String.valueOf(interviewApiDao.getCompany().getId()))
                    .addParameter("question_id", String.valueOf(currentQuestion.getId()))
                    .addParameter("job_id", String.valueOf(interviewApiDao.getJob().getId()))
                    .addParameter("device", "android")
                    .addParameter("device_type", Build.MODEL)
                    .addFileToUpload(new File(currentQuestion.getVideoPath()).getAbsolutePath(), "interview_video")
                    .setUtf8Charset()
                    .setNotificationConfig(notificationConfig)
                    .setAutoDeleteFilesAfterSuccessfulUpload(true)
                    .setDelegate(this)
                    .startUpload();

            Timber.d("VideoUploadInfoActivity %s", uploadId);
        } catch (Exception exc) {
            counter++;
            updateAdapter();
            doUploadVideo();
            Timber.d("VideoUploadInfoActivity %s", exc.getMessage());
        }
    }

    @Override
    public void onProgress(Context context, UploadInfo uploadInfo) {
        if (uploadInfo != null) {
            Timber.d("Video Upload Progress : %s", uploadInfo.getProgressPercent());
            astrntSDK.updateProgress(currentQuestion, uploadInfo.getProgressPercent());
        }
        updateAdapter();
    }

    @Override
    public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
        Timber.e("Video Upload Error : ");
        if (exception != null) {
            Timber.e("Video Upload Error : %s", exception.getMessage());
        }
        if (serverResponse != null && serverResponse.getBody() != null) {
            BaseApiDao baseApiDao = new Gson().fromJson(serverResponse.getBodyAsString(), BaseApiDao.class);
            Timber.e(baseApiDao.getMessage());
            Toast.makeText(context, baseApiDao.getMessage(), Toast.LENGTH_SHORT).show();
            astrntSDK.markAsCompressed(currentQuestion);
        }
        counter++;
        updateAdapter();
        doUploadVideo();
    }

    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
        Timber.d("Video Upload Complete");
        astrntSDK.markUploaded(currentQuestion);
        resetData();
    }

    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo) {
        Timber.d("Video Upload Canceled");
    }

    private void callFinish() {
        if (interviewApiDao.getJob().isRequireCv()) {
            cvStatus();
        }
        finishInterview();
    }

    private void showMenuDone() {
        menuShow = true;
        invalidateOptionsMenu();
    }

    private void resetData() {
        counter = 0;
        doUploadVideo();
        updateAdapter();
    }

    public void finishInterview() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        interviewRepository.finishInterview()
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
                    public void onApiResultOk(BaseApiDao apiDao) {
                        astrntSDK.setInterviewFinished();

                        swipeRefreshLayout.setEnabled(false);
                        swipeRefreshLayout.setRefreshing(false);
                        showMenuDone();
                        Toast.makeText(context, apiDao.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void cvStatus() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        interviewRepository.cvStatus()
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
                    public void onApiResultOk(BaseApiDao apiDao) {
                        if (apiDao.getMessage().equals("CV not uploaded")) {
                            cvStart();
                        } else {
                            Toast.makeText(context, apiDao.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                });
    }

    public void cvStart() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        interviewRepository.cvStart()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<BaseApiDao>() {

                    @Override
                    public void onApiResultCompleted() {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onApiResultError(String message, String code) {
                    }

                    @Override
                    public void onApiResultOk(BaseApiDao apiDao) {
                        Toast.makeText(context, apiDao.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (!menuShow) {
            Toast.makeText(context, "You can't exit untill all video uploaded", Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }
}
