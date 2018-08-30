package co.astrnt.surveyapp.feature.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.astrnt.managersdk.core.MyObserver;
import co.astrnt.managersdk.dao.CompanyApiDao;
import co.astrnt.managersdk.dao.ListCompanyApiDao;
import co.astrnt.managersdk.repository.CompanyRepository;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;
import co.astrnt.surveyapp.feature.manager.adapter.CompanyAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ListCompanyActivity extends BaseActivity {

    private CompanyRepository mCompanyRepository;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    private CompanyAdapter companyAdapter;
    private List<CompanyApiDao> listCompany = new ArrayList<>();

    public static void start(Context context) {
        Intent intent = new Intent(context, ListCompanyActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_company);

        recyclerView = findViewById(R.id.recycler_view);

        getSupportActionBar().setTitle("List Company");

        mCompanyRepository = new CompanyRepository(getManagerApi());

        companyAdapter = new CompanyAdapter(context, listCompany);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(companyAdapter);

        getCompany();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_notification, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notification:
                ListNotificationActivity.start(context);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getCompany() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mCompanyRepository.getListCompany()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<ListCompanyApiDao>() {

                    @Override
                    public void onApiResultCompleted() {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onApiResultError(String message, String code) {
                        Timber.e(message);
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onApiResultOk(ListCompanyApiDao apiDao) {
                        Timber.d(apiDao.getMessage());
                        listCompany = apiDao.getIntegration_company_list();
                        companyAdapter.setData(listCompany);
                    }
                });
    }

}
