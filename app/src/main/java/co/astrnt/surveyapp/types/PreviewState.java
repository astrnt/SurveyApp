package co.astrnt.surveyapp.types;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static co.astrnt.surveyapp.widget.PreviewButtonView.STATE_DONE;
import static co.astrnt.surveyapp.widget.PreviewButtonView.STATE_FINISHED;
import static co.astrnt.surveyapp.widget.PreviewButtonView.STATE_PAUSE;
import static co.astrnt.surveyapp.widget.PreviewButtonView.STATE_PLAY;
import static co.astrnt.surveyapp.widget.PreviewButtonView.STATE_RETAKE;

@StringDef({STATE_PLAY, STATE_PAUSE, STATE_FINISHED, STATE_DONE, STATE_RETAKE})
@Retention(RetentionPolicy.SOURCE)
public @interface PreviewState {
}