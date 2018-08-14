package co.astrnt.samplemanagersdk.feature.adapter;

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
import co.astrnt.samplemanagersdk.R;
import co.astrnt.samplemanagersdk.feature.VideoPreviewActivity;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ExampleViewHolder> {

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
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video, parent, false);
        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        VideoApiDao pokemon = this.listData.get(position);
        holder.onBind(pokemon);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ExampleViewHolder extends RecyclerView.ViewHolder {

        private TextView txtQuestionTitle;
        private ImageView imgThumbnail;

        private VideoApiDao item;

        ExampleViewHolder(View itemView) {
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
                    .resize(75, 75)
                    .centerCrop()
                    .placeholder(R.drawable.ic_broken_image_black_24dp)
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .into(imgThumbnail);
        }
    }
}
