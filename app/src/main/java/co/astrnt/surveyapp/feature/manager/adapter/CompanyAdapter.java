package co.astrnt.surveyapp.feature.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;

import java.util.List;

import co.astrnt.managersdk.dao.CompanyApiDao;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.feature.manager.ListJobActivity;
import co.astrnt.surveyapp.utils.PreferenceKey;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.JobViewHolder> {

    private List<CompanyApiDao> listData;
    private Context context;

    public CompanyAdapter(Context context, List<CompanyApiDao> data) {
        this.context = context;
        listData = data;
    }

    public void setData(List<CompanyApiDao> data) {
        this.listData = data;
        notifyDataSetChanged();
    }

    @Override
    public JobViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_company, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(JobViewHolder holder, int position) {
        CompanyApiDao data = this.listData.get(position);
        holder.onBind(data);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class JobViewHolder extends RecyclerView.ViewHolder {

        private TextView txtCompany;

        private CompanyApiDao item;

        JobViewHolder(View itemView) {
            super(itemView);
            txtCompany = itemView.findViewById(R.id.txt_company_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Hawk.put(PreferenceKey.SELECTED_COMPANY, item);
                    ListJobActivity.start(context);
                }
            });
        }

        void onBind(CompanyApiDao item) {
            this.item = item;
            txtCompany.setText(item.getName());
        }
    }
}
