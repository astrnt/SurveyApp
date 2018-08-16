package co.astrnt.surveyapp.feature.candidate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

import co.astrnt.qasdk.core.MyObserver;
import co.astrnt.qasdk.dao.BaseApiDao;
import co.astrnt.qasdk.dao.QuestionApiDao;
import co.astrnt.qasdk.repository.QuestionRepository;
import co.astrnt.qasdk.videocompressor.services.VideoCompressService;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;
import co.astrnt.surveyapp.utils.ServiceUtils;
import co.astrnt.surveyapp.widget.PreviewButtonView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class VideoPreviewRecordActivity extends BaseActivity implements PreviewButtonView.PreviewListener {

    private static final String EXT_VIDEO_URI = "VideoPreviewRecordActivity.VideoUri";

    private QuestionRepository mQuestionRepository;

    private TextView txtTitle;
    private TextView txtQuestion;
    private VideoView videoView;
    private TextView txtAttemptInfo;
    private PreviewButtonView previewButtonView;

    private ProgressDialog progressDialog;

    private QuestionApiDao currentQuestion;
    private CountDownTimer countDownTimer;
    private Uri videoUri;
    private long videoDuration;
    private int questionAttempt;

    public static void start(Context context, Uri uri) {
        Intent intent = new Intent(context, VideoPreviewRecordActivity.class);
        intent.putExtra(EXT_VIDEO_URI, uri);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_preview_record);

        txtTitle = findViewById(R.id.txt_title);
        txtQuestion = findViewById(R.id.txt_question);
        videoView = findViewById(R.id.video_view);
        txtAttemptInfo = findViewById(R.id.txt_attempt_info);
        previewButtonView = findViewById(R.id.btn_preview);

        mQuestionRepository = new QuestionRepository(getApi());

        currentQuestion = videoSDK.getCurrentQuestion();

        videoUri = getIntent().getParcelableExtra(EXT_VIDEO_URI);
        videoView.setVideoURI(videoUri);

        previewButtonView.setPreviewListener(this);

        showInfo();
        prepareVideoPlayer();
    }

    private void showInfo() {

        questionAttempt = videoSDK.getQuestionAttempt();
        currentQuestion = videoSDK.getCurrentQuestion();

        txtQuestion.setText(currentQuestion.getTitle());
        txtTitle.setText("Preview");

        if (videoSDK.isLastAttempt()) {
            previewButtonView.setCurrentState(PreviewButtonView.STATE_DONE);
            txtAttemptInfo.setVisibility(View.GONE);
            finishQuestion(currentQuestion);
        } else {
            String attemptInfo = context.getResources().getQuantityString(R.plurals.you_have_more_attempt, questionAttempt, questionAttempt);
            txtAttemptInfo.setText(attemptInfo);
            txtAttemptInfo.setVisibility(View.VISIBLE);
        }

    }

    private void prepareVideoPlayer() {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                videoDuration = mp.getDuration();

                previewButtonView.setMaxProgress((int) videoDuration / 1000);

                ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                float videoWidth = mp.getVideoWidth();
                float videoHeight = mp.getVideoHeight();
                float viewWidth = videoView.getWidth();
                lp.height = (int) (viewWidth * (videoHeight / videoWidth));
                videoView.setLayoutParams(lp);
                previewButtonView.setCurrentState(PreviewButtonView.STATE_PLAY);
                onVideoPlay();
            }
        });
    }

    private void playVideo() {
        if (videoView.isPlaying()) {
            countDownTimer.cancel();
            return;
        }
        //TODO : video view check resume state
        videoView.start();
    }

    @Override
    public void onVideoFinished() {
        countDownTimer.cancel();
    }

    @Override
    public void onVideoPlay() {
        playVideo();
        countDownTimer = new CountDownTimer(videoDuration, 1000) {

            public void onTick(long millisUntilFinished) {
                long currentProgress = millisUntilFinished / 1000;
                previewButtonView.setCurrentProgress(currentProgress);
            }

            public void onFinish() {
                previewButtonView.setCurrentProgress(0);
                previewButtonView.setCurrentState(PreviewButtonView.STATE_FINISHED);
            }
        }.start();
    }

    @Override
    public void onVideoPause() {
        videoView.pause();
        countDownTimer.cancel();
    }

    @Override
    public void onVideoRetake() {
        File file = new File(videoUri.getPath());
        file.delete();
        VideoRecordActivity.start(context);
        finish();
    }

    @Override
    public void onVideoDone() {
        if (videoSDK.isNotLastQuestion()) {
            videoSDK.increaseQuestionIndex();
        }
        showNextQuestion();
        compressVideo();
    }

    public void finishQuestion(QuestionApiDao currentQuestion) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mQuestionRepository.finishQuestion(currentQuestion)
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
                        onVideoDone();
                    }
                });
    }

    private void compressVideo() {
        File file = new File(videoUri.getPath());
        videoSDK.markAsPending(currentQuestion, videoUri.getPath());
        if (!ServiceUtils.isMyServiceRunning(context, VideoCompressService.class)) {
            VideoCompressService.start(context, file.getAbsolutePath(), currentQuestion.getId());
        } else {
            showNextQuestion();
        }
    }

    private void showNextQuestion() {
        if (videoSDK.isNotLastQuestion()) {
            VideoInstructionActivity.start(context);
        } else {
//            TODO: video upload
            Toast.makeText(context, "Interview Already Finished", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
