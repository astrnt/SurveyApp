package co.astrnt.surveyapp.feature.manager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;

import java.util.List;

import co.astrnt.managersdk.dao.CompanyApiDao;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.utils.PreferenceKey;

public class CompanySpinnerAdapter extends ArrayAdapter<CompanyApiDao> {

    private Context context;
    private List<CompanyApiDao> data;
    private CompanyApiDao selectedCompany;

    public CompanySpinnerAdapter(Context context, int textViewResourceId, List<CompanyApiDao> data) {
        super(context, textViewResourceId, data);
        this.context = context;
        this.data = data;
        selectedCompany = Hawk.get(PreferenceKey.SELECTED_COMPANY);
    }

    public void setData(List<CompanyApiDao> data) {
        this.data = data;
        selectedCompany = Hawk.get(PreferenceKey.SELECTED_COMPANY);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public CompanyApiDao getItem(int position) {
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
        label.setText(data.get(position).getName());

        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getDropDownView(position, convertView, parent);
        label.setTextColor(Color.BLACK);

        CompanyApiDao item = data.get(position);

        if (selectedCompany != null) {
            if (item.getName().equals(selectedCompany.getName())) {
                label.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_green_24dp, 0, 0, 0);
            } else {
                label.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_white_24dp, 0, 0, 0);
            }
        } else {
            if (position == 0) {

                label.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_green_24dp, 0, 0, 0);
            } else {
                label.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_white_24dp, 0, 0, 0);
            }
        }

        label.setText(item.getName());

        return label;
    }
}