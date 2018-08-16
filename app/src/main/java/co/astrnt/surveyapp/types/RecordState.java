package co.astrnt.surveyapp.types;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static co.astrnt.surveyapp.widget.RecordButtonView.STATE_ON_COUNTDOWN;
import static co.astrnt.surveyapp.widget.RecordButtonView.STATE_ON_FINISH;
import static co.astrnt.surveyapp.widget.RecordButtonView.STATE_ON_RECORD;
import static co.astrnt.surveyapp.widget.RecordButtonView.STATE_PRE_RECORD;

@StringDef({STATE_PRE_RECORD, STATE_ON_COUNTDOWN, STATE_ON_RECORD, STATE_ON_FINISH})
@Retention(RetentionPolicy.SOURCE)
public @interface RecordState {
}