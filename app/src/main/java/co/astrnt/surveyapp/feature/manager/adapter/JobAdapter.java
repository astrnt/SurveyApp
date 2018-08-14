package co.astrnt.surveyapp.feature.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.astrnt.managersdk.dao.JobApiDao;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.feature.manager.DetailJobActivity;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.ExampleViewHolder> {

    private List<JobApiDao> listData;
    private Context context;

    public JobAdapter(Context context, List<JobApiDao> data) {
        this.context = context;
        listData = data;
    }

    public void setData(List<JobApiDao> data) {
        this.listData = data;
        notifyDataSetChanged();
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job, parent, false);
        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        JobApiDao pokemon = this.listData.get(position);
        holder.onBind(pokemon);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ExampleViewHolder extends RecyclerView.ViewHolder {

        private TextView txtJobTitle;
        private TextView txtJobType;

        private JobApiDao item;

        ExampleViewHolder(View itemView) {
            super(itemView);
            txtJobTitle = itemView.findViewById(R.id.txt_job_title);
            txtJobType = itemView.findViewById(R.id.txt_job_type);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DetailJobActivity.start(context, item);
                }
            });
        }

        void onBind(JobApiDao item) {
            this.item = item;
            txtJobTitle.setText(item.getJob_name());
            txtJobType.setText(item.getJob_type() + " - "  + item.getLocations());
        }
    }
}
