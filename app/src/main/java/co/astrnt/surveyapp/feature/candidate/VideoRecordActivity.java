package co.astrnt.surveyapp.feature.candidate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;

import java.io.File;

import co.astrnt.qasdk.core.MyObserver;
import co.astrnt.qasdk.dao.BaseApiDao;
import co.astrnt.qasdk.dao.QuestionApiDao;
import co.astrnt.qasdk.repository.QuestionRepository;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;
import co.astrnt.surveyapp.widget.RecordButtonView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static co.astrnt.surveyapp.widget.RecordButtonView.STATE_ON_FINISH;
import static co.astrnt.surveyapp.widget.RecordButtonView.STATE_ON_RECORD;

public class VideoRecordActivity extends BaseActivity implements RecordButtonView.RecordListener {

    private QuestionRepository mQuestionRepository;

    private TextView txtTitle;
    private TextView txtQuestion;
    private TextView txtCountDown;
    private RecordButtonView btnRecord;
    private CameraView cameraView;

    private ProgressDialog progressDialog;

    private QuestionApiDao currentQuestion;
    private CountDownTimer countDownTimer;
    private File recordFile;

    public static void start(Context context) {
        Intent intent = new Intent(context, VideoRecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_record);

        txtTitle = findViewById(R.id.txt_title);
        txtQuestion = findViewById(R.id.txt_question);
        txtCountDown = findViewById(R.id.txt_count_down);
        cameraView = findViewById(R.id.camera_view);
        btnRecord = findViewById(R.id.btn_record);

        mQuestionRepository = new QuestionRepository(getApi());

        currentQuestion = videoSDK.getCurrentQuestion();

        btnRecord.setRecordListener(this);
        btnRecord.setQuestion(currentQuestion);

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRecord.setToNextState();
            }
        });

        setUpCamera();
    }

    private void setUpCamera() {
        cameraView.addCameraListener(new CameraListener() {

            @Override
            public void onVideoTaken(File video) {
                recordFile = video;
                moveToPreview();
            }
        });
    }

    private void startRecording() {
        File directory = new File(context.getFilesDir(), "video");
        if (!directory.exists()) {
            directory.mkdir();
        }
        recordFile = new File(directory, currentQuestion.getId() + "_video.mp4");
        cameraView.setVideoMaxDuration(currentQuestion.getMaxTime() * 1000);
        cameraView.startCapturingVideo(recordFile);
    }

    private void decreaseAttempt() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mQuestionRepository.addQuestionAttempt(currentQuestion)
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
                    public void onApiResultOk(BaseApiDao baseApiDao) {
                        Toast.makeText(context, baseApiDao.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onPreRecord() {
        txtTitle.setText("Instruction");
        txtQuestion.setText("Record Instruction");
    }

    @Override
    public void onCountdown() {
        txtTitle.setText("Question");
        decreaseAttempt();
        txtQuestion.setText(currentQuestion.getTitle());
        videoSDK.decreaseQuestionAttempt();
        if (videoSDK.isLastAttempt()) {
            videoSDK.markNotAnswer(currentQuestion);
        }
        txtCountDown.setText(String.valueOf(btnRecord.getMaxProgress()));
        txtCountDown.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(btnRecord.getMaxProgress() * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                long currentProgress = millisUntilFinished / 1000;
                txtCountDown.setText(String.valueOf(currentProgress + 1));
                btnRecord.setCurrentProgress(currentProgress);
            }

            public void onFinish() {
                txtCountDown.setText(String.valueOf(1));
                txtCountDown.setVisibility(View.GONE);
                btnRecord.setCurrentProgress(0);
                btnRecord.setCurrentState(STATE_ON_RECORD);
            }
        }.start();
    }

    @Override
    public void onRecording() {
        txtTitle.setText("Recording");
        startRecording();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                countDownTimer = new CountDownTimer(currentQuestion.getMaxTime() * 1000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        long currentProgress = millisUntilFinished / 1000;
                        btnRecord.setCurrentProgress(currentProgress);
                        if (currentProgress < 5) {
                            if (txtCountDown.getVisibility() == View.GONE) {
                                txtCountDown.setVisibility(View.VISIBLE);
                            }
                            txtCountDown.setText(String.valueOf(currentProgress + 1));
                        }
                    }

                    public void onFinish() {
                        btnRecord.setCurrentProgress(0);
                        btnRecord.setCurrentState(STATE_ON_FINISH);
                    }
                }.start();
            }
        }, 2000);
    }

    @Override
    public void onFinished() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        cameraView.stopCapturingVideo();
        txtCountDown.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }

    private void moveToPreview() {
        if (recordFile != null) {
            VideoPreviewRecordActivity.start(context, Uri.fromFile(recordFile));
            finish();
        }
    }
}
