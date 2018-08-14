package co.astrnt.surveyapp.feature.candidate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;

import co.astrnt.qasdk.dao.JobApiDao;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;
import co.astrnt.surveyapp.utils.WordUtils;

public class JobDescriptionActivity extends BaseActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, JobDescriptionActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_job_description);

        TextView txtJobLocation = findViewById(R.id.txt_location);
        TextView txtJobTitle = findViewById(R.id.txt_job_title);
        TextView txtDescription = findViewById(R.id.txt_description);
        TextView txtResponsibility = findViewById(R.id.txt_responsibility);
        TextView txtRequirement = findViewById(R.id.txt_requirement);
        TextView txtDeadline = findViewById(R.id.txt_deadline);
        TextView txtTotalCandidates = findViewById(R.id.txt_total_candidates);

        JobApiDao jobApiDao = interviewApiDao.getJob();

        txtJobTitle.setText(jobApiDao.getTitle());
        txtJobLocation.setText(WordUtils.getTypeAndLocation(jobApiDao));

        if (TextUtils.isEmpty(jobApiDao.getDescription())) {
            txtDescription.setText(R.string.not_available);
        } else {
            txtDescription.setText(jobApiDao.getDescription());
        }
        if (TextUtils.isEmpty(jobApiDao.getResponsibility())) {
            txtResponsibility.setText(R.string.not_available);
        } else {
            txtResponsibility.setText(jobApiDao.getResponsibility());
        }

        if (TextUtils.isEmpty(jobApiDao.getRequirement())) {
            txtRequirement.setText(R.string.not_available);
        } else {
            txtRequirement.setText(jobApiDao.getRequirement());
        }

    }


}
