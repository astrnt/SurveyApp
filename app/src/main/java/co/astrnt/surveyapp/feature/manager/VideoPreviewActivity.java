package co.astrnt.surveyapp.feature.manager;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;

public class VideoPreviewActivity extends BaseActivity {

    private static final String EXT_QUESTION_TITLE = "QUESTION_TITLE";
    private static final String EXT_VIDEO_URL = "QUESTION_TITLE";

    private VideoView videoView;

    public static void start(Context context, String questionTitle, String videoUrl) {
        Intent intent = new Intent(context, VideoPreviewActivity.class);
        intent.putExtra(EXT_QUESTION_TITLE, questionTitle);
        intent.putExtra(EXT_VIDEO_URL, videoUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Video Preview");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setContentView(R.layout.activity_video_preview);

        String questionTitle = getIntent().getStringExtra(EXT_QUESTION_TITLE);
        String videoUrl = getIntent().getStringExtra(EXT_VIDEO_URL);

        videoView = findViewById(R.id.video_view);
        TextView txtQuestionTitle = findViewById(R.id.txt_question_title);

        txtQuestionTitle.setText(questionTitle);
        videoView.setVideoPath(videoUrl);
        prepareVideoPlayer();
    }

    private void prepareVideoPlayer() {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                float videoWidth = mp.getVideoWidth();
                float videoHeight = mp.getVideoHeight();
                float viewWidth = videoView.getWidth();
                lp.height = (int) (viewWidth * (videoHeight / videoWidth));
                videoView.setLayoutParams(lp);
            }
        });
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }
        videoView.start();
    }
}
