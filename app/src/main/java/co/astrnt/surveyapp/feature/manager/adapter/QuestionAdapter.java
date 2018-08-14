package co.astrnt.surveyapp.feature.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.astrnt.managersdk.dao.JobApiDao;
import co.astrnt.managersdk.dao.QuestionApiDao;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.feature.manager.DetailQuestionActivity;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ExampleViewHolder> {

    private List<QuestionApiDao> listData;
    private JobApiDao jobApiDao;
    private Context context;

    public QuestionAdapter(Context context, JobApiDao jobApiDao, List<QuestionApiDao> data) {
        this.context = context;
        this.jobApiDao = jobApiDao;
        listData = data;
    }

    public void setData(List<QuestionApiDao> data) {
        this.listData = data;
        notifyDataSetChanged();
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false);
        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        QuestionApiDao pokemon = this.listData.get(position);
        holder.onBind(pokemon);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class ExampleViewHolder extends RecyclerView.ViewHolder {

        private TextView txtQuestionTitle;
        private TextView txtTakeCountAndMaxTime;

        private QuestionApiDao item;

        ExampleViewHolder(View itemView) {
            super(itemView);
            txtQuestionTitle = itemView.findViewById(R.id.txt_question_title);
            txtTakeCountAndMaxTime = itemView.findViewById(R.id.txt_takes_and_max_time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DetailQuestionActivity.start(context, jobApiDao, item);
                }
            });
        }

        void onBind(QuestionApiDao item) {
            this.item = item;
            txtQuestionTitle.setText(item.getTitle());
            txtTakeCountAndMaxTime.setText(item.getMaxTime() + "s - "  + item.getTakesCount() + " attempt");
        }
    }
}
