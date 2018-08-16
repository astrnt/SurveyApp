package co.astrnt.surveyapp.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.types.PreviewState;
import rjsv.circularview.CircleView;

public class PreviewButtonView extends LinearLayout implements View.OnClickListener {

    public static final String STATE_PLAY = "play";
    public static final String STATE_PAUSE = "pause";
    public static final String STATE_FINISHED = "finished";
    public static final String STATE_DONE = "done";
    public static final String STATE_RETAKE = "retake";

    private CircleView circleProgress;
    private ImageView icPausePlay;
    private LinearLayout btnDone;
    private RelativeLayout btnPausePlay;
    private LinearLayout btnRetake;

    private @PreviewState String currentState = STATE_PLAY;
    private PreviewListener previewListener;
    private int currentProgress;
    private int maxProgress;

    public PreviewButtonView(Context context) {
        super(context);
        init();
    }

    public PreviewButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PreviewButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PreviewButtonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_preview_button, this);
        circleProgress = view.findViewById(R.id.circle_progress);
        icPausePlay = view.findViewById(R.id.ic_pause_play);
        btnDone = view.findViewById(R.id.btn_done);
        btnPausePlay = view.findViewById(R.id.btn_pause_play);
        btnRetake = view.findViewById(R.id.btn_retake);

        btnDone.setOnClickListener(this);
        btnPausePlay.setOnClickListener(this);
        btnRetake.setOnClickListener(this);

        circleProgress.setProgressValue(0);
    }

    public void setPreviewListener(PreviewListener previewListener) {
        this.previewListener = previewListener;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(long currentProgress) {
        this.currentProgress = (int) currentProgress;
        circleProgress.setProgressValue(currentProgress);
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        circleProgress.setMaximumValue(maxProgress);
    }

    private void setIconPlay() {
        icPausePlay.setImageResource(R.drawable.ic_play);
    }

    private void setIconPause() {
        icPausePlay.setImageResource(R.drawable.ic_pause);
    }

    public @PreviewState
    String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(@PreviewState String currentState) {
        this.currentState = currentState;
        if (previewListener != null) {
            switch (getCurrentState()) {
                case STATE_PAUSE:
                    setIconPlay();
                    circleProgress.setProgressValue(getCurrentProgress());
                    previewListener.onVideoPause();
                    break;
                case STATE_PLAY:
                    setIconPause();
                    circleProgress.setProgressValue(getMaxProgress());
                    previewListener.onVideoPlay();
                    break;
                case STATE_FINISHED:
                    setIconPlay();
                    circleProgress.setProgressValue(0);
                    previewListener.onVideoFinished();
                    break;
                case STATE_DONE:
                    previewListener.onVideoDone();
                    break;
                case STATE_RETAKE:
                    previewListener.onVideoRetake();
                    break;
                default:
                    break;
            }
        }
    }

    private void onPausePlayClick() {
        switch (getCurrentState()) {
            case STATE_PAUSE:
                setIconPlay();
                setCurrentState(STATE_PLAY);
                break;
            case STATE_PLAY:
                setIconPause();
                setCurrentState(STATE_PAUSE);
                break;
            case STATE_FINISHED:
                setIconPause();
                setCurrentState(STATE_PLAY);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_retake:
                setCurrentState(STATE_RETAKE);
                break;
            case R.id.btn_pause_play:
                onPausePlayClick();
                break;
            case R.id.btn_done:
                setCurrentState(STATE_DONE);
                break;
        }
    }

    public interface PreviewListener {
        void onVideoFinished();

        void onVideoPlay();

        void onVideoPause();

        void onVideoRetake();

        void onVideoDone();
    }
}
