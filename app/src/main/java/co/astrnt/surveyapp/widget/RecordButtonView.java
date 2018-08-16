package co.astrnt.surveyapp.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.astrnt.qasdk.dao.QuestionApiDao;
import co.astrnt.surveyapp.BuildConfig;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.types.RecordState;
import rjsv.circularview.CircleView;

public class RecordButtonView extends LinearLayout {

    public static final String STATE_PRE_RECORD = "pre_record";
    public static final String STATE_ON_COUNTDOWN = "on_countdown";
    public static final String STATE_ON_RECORD = "on_record";
    public static final String STATE_ON_FINISH = "on_finish";

    private CircleView circleProgress;
    private View bgCircle;
    private TextView txtState;

    private @RecordState String currentState = STATE_PRE_RECORD;
    private RecordListener recordListener;
    private int currentProgress;
    private int maxProgress;
    private QuestionApiDao question;

    public RecordButtonView(Context context) {
        super(context);
        init();
    }

    public RecordButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RecordButtonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_record_button, this);
        circleProgress = view.findViewById(R.id.circle_progress);
        bgCircle = view.findViewById(R.id.bg_circle);
        txtState = view.findViewById(R.id.txt_state);
        circleProgress.setProgressValue(0);
    }

    public void setQuestion(QuestionApiDao question) {
        this.question = question;
    }

    public void setRecordListener(RecordListener recordListener) {
        this.recordListener = recordListener;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(long currentProgress) {
        this.currentProgress = (int) currentProgress;
        circleProgress.setProgressValue(currentProgress);
        if (getCurrentState().equals(STATE_ON_RECORD)) {
            if (getCurrentProgress() > (question.getMaxTime() - 5)) {
                bgCircle.setVisibility(GONE);
                txtState.setVisibility(VISIBLE);
                txtState.setText(String.valueOf(currentProgress));
            }
            if (getCurrentProgress() <= (question.getMaxTime() - 5)) {
                setClickable(true);
                bgCircle.setVisibility(VISIBLE);
                txtState.setVisibility(VISIBLE);
                txtState.setText(R.string.finish);
            }
        }
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        circleProgress.setMaximumValue(maxProgress);
    }

    public @RecordState
    String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(@RecordState String currentState) {
        this.currentState = currentState;
        if (recordListener != null) {
            switch (getCurrentState()) {
                case STATE_PRE_RECORD:
                    circleProgress.setProgressValue(0);
                    txtState.setText(R.string.start);
                    txtState.setVisibility(VISIBLE);
                    bgCircle.setVisibility(GONE);
                    recordListener.onPreRecord();
                    break;
                case STATE_ON_COUNTDOWN:
                    txtState.setVisibility(GONE);
                    if (BuildConfig.DEBUG) {
                        circleProgress.setProgressValue(3);
                        setMaxProgress(3);
                    } else {
                        circleProgress.setProgressValue(10);
                        setMaxProgress(10);
                    }
                    recordListener.onCountdown();
                    break;
                case STATE_ON_RECORD:
                    circleProgress.setProgressValue(question.getMaxTime());
                    setMaxProgress(question.getMaxTime());
                    recordListener.onRecording();
                    break;
                case STATE_ON_FINISH:
                    circleProgress.setProgressValue(0);
                    recordListener.onFinished();
                    break;
                default:
                    break;
            }
        }
    }

    public void setToNextState() {
        switch (getCurrentState()) {
            case STATE_PRE_RECORD:
                setClickable(false);
                setCurrentState(STATE_ON_COUNTDOWN);
                break;
            case STATE_ON_COUNTDOWN:
                setCurrentState(STATE_ON_RECORD);
                break;
            case STATE_ON_RECORD:
                setCurrentState(STATE_ON_FINISH);
                break;
            case STATE_ON_FINISH:
                setCurrentState(STATE_PRE_RECORD);
                break;
            default:
                break;
        }
    }

    public interface RecordListener {
        void onPreRecord();

        void onCountdown();

        void onRecording();

        void onFinished();
    }
}
