package co.astrnt.surveyapp.feature.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.astrnt.managersdk.core.MyObserver;
import co.astrnt.managersdk.dao.CompanyApiDao;
import co.astrnt.managersdk.dao.ListCompanyApiDao;
import co.astrnt.managersdk.dao.ListNotificationApiDao;
import co.astrnt.managersdk.dao.NotificationApiDao;
import co.astrnt.managersdk.repository.CompanyRepository;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;
import co.astrnt.surveyapp.feature.manager.adapter.CompanyAdapter;
import co.astrnt.surveyapp.feature.manager.adapter.NotificationAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ListNotificationActivity extends BaseActivity {

    private CompanyRepository mCompanyRepository;
    private AppCompatSpinner spnCompany;
    private RecyclerView recyclerView;
    private CompanyAdapter companyAdapter;
    private NotificationAdapter notificationAdapter;
    private ProgressDialog progressDialog;

    private CompanyApiDao selectedCompany;
    private List<CompanyApiDao> listCompany = new ArrayList<>();
    private List<NotificationApiDao> listNotification = new ArrayList<>();

    public static void start(Context context) {
        Intent intent = new Intent(context, ListNotificationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_notification);

        getSupportActionBar().setTitle("List Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        spnCompany = findViewById(R.id.spn_company);
        recyclerView = findViewById(R.id.recycler_view);

        mCompanyRepository = new CompanyRepository(getManagerApi());

        companyAdapter = new CompanyAdapter(context, android.R.layout.simple_spinner_dropdown_item, listCompany);
        spnCompany.setAdapter(companyAdapter);
        spnCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCompany = companyAdapter.getItem(position);
                getNotification("0");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        notificationAdapter = new NotificationAdapter(context, listNotification);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(notificationAdapter);

        getCompany();
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

    private void getNotification(String lastNotificationId) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mCompanyRepository.getListNotification(selectedCompany.getCompany_identifier(), lastNotificationId, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<ListNotificationApiDao>() {

                    @Override
                    public void onApiResultCompleted() {
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onApiResultError(String message, String code) {
                        Timber.e(message);
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        listNotification.clear();
                        notificationAdapter.setData(listNotification);
                    }

                    @Override
                    public void onApiResultOk(ListNotificationApiDao apiDao) {
                        Timber.d(apiDao.getMessage());
                        listNotification = apiDao.getNotification();
                        notificationAdapter.setData(listNotification);
                    }
                });
    }

}
