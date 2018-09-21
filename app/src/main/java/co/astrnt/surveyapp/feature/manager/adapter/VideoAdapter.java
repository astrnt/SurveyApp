package co.astrnt.surveyapp.feature.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import co.astrnt.managersdk.dao.VideoApiDao;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.feature.manager.VideoPreviewActivity;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<VideoApiDao> listData;
    private Context context;

    public VideoAdapter(Context context, List<VideoApiDao> data) {
        this.context = context;
        listData = data;
    }

    public void setData(List<VideoApiDao> data) {
        this.listData = data;
        notifyDataSetChanged();
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        VideoApiDao item = this.listData.get(position);
        holder.onBind(item);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {

        private TextView txtQuestionTitle;
        private ImageView imgThumbnail;

        private VideoApiDao item;

        VideoViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
            txtQuestionTitle = itemView.findViewById(R.id.txt_question_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoPreviewActivity.start(context, item.getQuestion_title(), item.getVideo_url());
                }
            });
        }

        void onBind(VideoApiDao item) {
            this.item = item;
            txtQuestionTitle.setText(item.getQuestion_title());
            Picasso.get().load(item.getThumbnail_url())
                    .resize(120, 98)
                    .centerCrop()
                    .into(imgThumbnail);
        }
    }
}
