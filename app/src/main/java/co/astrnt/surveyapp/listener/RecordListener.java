package co.astrnt.surveyapp.listener;

public interface RecordListener {
    void onPreRecord();

    void onCountdown();

    void onRecording();

    void onFinished();
}