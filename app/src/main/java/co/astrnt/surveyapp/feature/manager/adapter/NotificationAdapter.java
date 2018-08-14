package co.astrnt.samplemanagersdk.feature.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.astrnt.managersdk.dao.NotificationApiDao;
import co.astrnt.samplemanagersdk.R;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ExampleViewHolder> {

    private List<NotificationApiDao> listData;
    private Context context;

    public NotificationAdapter(Context context, List<NotificationApiDao> data) {
        this.context = context;
        listData = data;
    }

    public void setData(List<NotificationApiDao> data) {
        this.listData = data;
        notifyDataSetChanged();
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        NotificationApiDao pokemon = this.listData.get(position);
        holder.onBind(pokemon);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ExampleViewHolder extends RecyclerView.ViewHolder {

        private TextView txtNotification;
        private TextView txtJobName;
        private TextView txtDate;

        private NotificationApiDao item;

        ExampleViewHolder(View itemView) {
            super(itemView);
            txtNotification = itemView.findViewById(R.id.txt_notification);
            txtJobName = itemView.findViewById(R.id.txt_job_name);
            txtDate = itemView.findViewById(R.id.txt_date);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    TODO: notification detail
                }
            });
        }

        void onBind(NotificationApiDao item) {
            this.item = item;
            txtNotification.setText(item.getQuestionText());
            txtJobName.setText(item.getJobName());
            txtDate.setText(item.getTimestamp());
        }
    }
}
