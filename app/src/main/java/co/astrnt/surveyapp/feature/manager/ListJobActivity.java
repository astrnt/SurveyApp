package co.astrnt.surveyapp.feature.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.astrnt.managersdk.core.MyObserver;
import co.astrnt.managersdk.dao.CompanyApiDao;
import co.astrnt.managersdk.dao.JobApiDao;
import co.astrnt.managersdk.dao.ListCompanyApiDao;
import co.astrnt.managersdk.dao.ListJobApiDao;
import co.astrnt.managersdk.repository.CompanyRepository;
import co.astrnt.managersdk.repository.JobRepository;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;
import co.astrnt.surveyapp.feature.manager.adapter.CompanyAdapter;
import co.astrnt.surveyapp.feature.manager.adapter.JobAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ListJobActivity extends BaseActivity {

    private JobRepository mJobRepository;
    private CompanyRepository mCompanyRepository;
    private AppCompatSpinner spnCompany;
    private RecyclerView recyclerView;
    private FloatingActionButton fabCreateJob;
    private JobAdapter jobAdapter;
    private ProgressDialog progressDialog;

    private CompanyAdapter companyAdapter;
    private CompanyApiDao selectedCompany;
    private List<CompanyApiDao> listCompany = new ArrayList<>();
    private List<JobApiDao> listJob = new ArrayList<>();

    public static void start(Context context) {
        Intent intent = new Intent(context, ListJobActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_job);

        spnCompany = findViewById(R.id.spn_company);
        recyclerView = findViewById(R.id.recycler_view);
        fabCreateJob = findViewById(R.id.fab_add);

        mJobRepository = new JobRepository(getManagerApi());
        mCompanyRepository = new CompanyRepository(getManagerApi());

        jobAdapter = new JobAdapter(context, listJob);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL);
        recyclerView.addItemDecoration(decoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(jobAdapter);

        fabCreateJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateJobActivity.start(context);
            }
        });

        companyAdapter = new CompanyAdapter(context, android.R.layout.simple_spinner_dropdown_item, listCompany);
        spnCompany.setAdapter(companyAdapter);
        spnCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCompany = companyAdapter.getItem(position);
                getData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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

    private void getData() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mJobRepository.getListJobByCompany(selectedCompany.getCompany_identifier())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<ListJobApiDao>() {

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
                    public void onApiResultOk(ListJobApiDao apiDao) {
                        Timber.d(apiDao.getMessage());
                        listJob = apiDao.getJobs();
                        jobAdapter.setData(listJob);
                    }
                });
    }

}
