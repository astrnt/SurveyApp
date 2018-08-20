package co.astrnt.surveyapp.feature.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.astrnt.managersdk.dao.CandidateApiDao;
import co.astrnt.managersdk.dao.JobApiDao;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.feature.manager.ListVideoActivity;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder> {

    private List<CandidateApiDao> listData;
    private Context context;
    private JobApiDao jobApiDao;

    public CandidateAdapter(Context context, JobApiDao jobApiDao, List<CandidateApiDao> data) {
        this.context = context;
        this.jobApiDao = jobApiDao;
        listData = data;
    }

    public void setData(List<CandidateApiDao> data) {
        this.listData = data;
        notifyDataSetChanged();
    }

    @Override
    public CandidateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_candidate, parent, false);
        return new CandidateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CandidateViewHolder holder, int position) {
        CandidateApiDao data = this.listData.get(position);
        holder.onBind(data);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class CandidateViewHolder extends RecyclerView.ViewHolder {

        private TextView txtCandidateName;
        private TextView txtEmail;

        private CandidateApiDao item;

        CandidateViewHolder(View itemView) {
            super(itemView);
            txtCandidateName = itemView.findViewById(R.id.txt_candidate_name);
            txtEmail = itemView.findViewById(R.id.txt_email);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ListVideoActivity.start(context, jobApiDao, item.getCandidate_identifier());
                }
            });
        }

        void onBind(CandidateApiDao item) {
            this.item = item;
            txtCandidateName.setText(item.getName());
            txtEmail.setText(item.getEmail());
        }
    }
}
