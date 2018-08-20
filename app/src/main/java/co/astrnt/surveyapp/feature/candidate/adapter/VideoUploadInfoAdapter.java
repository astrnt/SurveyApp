package co.astrnt.surveyapp.feature.candidate.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import java.util.List;

import co.astrnt.qasdk.dao.QuestionApiDao;
import co.astrnt.qasdk.type.UploadStatusType;
import co.astrnt.surveyapp.R;

public class VideoUploadInfoAdapter extends RecyclerView.Adapter<VideoUploadInfoAdapter.VideoUploadInfoViewHolder> {

    private List<QuestionApiDao> listData;
    private Context context;

    public VideoUploadInfoAdapter(Context context, List<QuestionApiDao> data) {
        this.context = context;
        listData = data;
    }

    public void setData(List<QuestionApiDao> data) {
        this.listData = data;
        notifyDataSetChanged();
    }

    @Override
    public VideoUploadInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video_upload_info, parent, false);
        return new VideoUploadInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoUploadInfoViewHolder holder, int position) {
        QuestionApiDao data = this.listData.get(position);
        holder.onBind(data, position);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class VideoUploadInfoViewHolder extends RecyclerView.ViewHolder {

        private TextView txtUploadNo;
        private TextView txtUploadStatus;
        private ImageView imgUploadStatus;
        private RoundCornerProgressBar progressBar;

        private QuestionApiDao item;

        VideoUploadInfoViewHolder(View itemView) {
            super(itemView);
            txtUploadNo = itemView.findViewById(R.id.txt_upload_no);
            txtUploadStatus = itemView.findViewById(R.id.txt_upload_status);
            imgUploadStatus = itemView.findViewById(R.id.img_upload_status);
            progressBar = itemView.findViewById(R.id.progress_upload_status);
        }

        void onBind(QuestionApiDao item, int position) {
            this.item = item;
            txtUploadNo.setText(String.format("%d.", position + 1));

            Context context = progressBar.getContext();
            txtUploadStatus.setTextColor(ContextCompat.getColor(context, R.color.black33));
            switch (item.getUploadStatus()) {
                case UploadStatusType.PENDING:
                case UploadStatusType.COMPRESSED:
                    progressBar.setProgress(0);
                    progressBar.setProgressColor(ContextCompat.getColor(context, R.color.pending));
                    imgUploadStatus.setImageResource(R.drawable.ic_pending);
                    txtUploadStatus.setText(R.string.pending);
                    break;
                case UploadStatusType.NOT_ANSWER:
                    progressBar.setProgress(100);
                    progressBar.setProgressColor(ContextCompat.getColor(context, R.color.failed));
                    imgUploadStatus.setImageResource(R.drawable.ic_not_answer);
                    txtUploadStatus.setText(R.string.failed);
                    txtUploadStatus.setTextColor(ContextCompat.getColor(context, R.color.failed));
                    break;
                case UploadStatusType.UPLOADING:
                    progressBar.setProgress((int) item.getUploadProgress());
                    progressBar.setProgressColor(ContextCompat.getColor(context, R.color.uploading));
                    imgUploadStatus.setImageResource(R.drawable.ic_uploading);
                    txtUploadStatus.setText(R.string.uploading);
                    break;
                case UploadStatusType.UPLOADED:
                    progressBar.setProgress(100);
                    progressBar.setProgressColor(ContextCompat.getColor(context, R.color.complete));
                    imgUploadStatus.setImageResource(R.drawable.ic_uploaded);
                    txtUploadStatus.setText(R.string.complete);
                    break;
            }
        }
    }
}
