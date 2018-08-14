package co.astrnt.surveyapp.feature.manager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import co.astrnt.managersdk.dao.JobTypeApiDao;

public class JobTypeAdapter extends ArrayAdapter<JobTypeApiDao> {

    private Context context;
    private List<JobTypeApiDao> data;

    public JobTypeAdapter(Context context, int textViewResourceId, List<JobTypeApiDao> data) {
        super(context, textViewResourceId, data);
        this.context = context;
        this.data = data;
    }

    public void setData(List<JobTypeApiDao> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public JobTypeApiDao getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(data.get(position).getTitle());

        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(data.get(position).getTitle());

        return label;
    }
}