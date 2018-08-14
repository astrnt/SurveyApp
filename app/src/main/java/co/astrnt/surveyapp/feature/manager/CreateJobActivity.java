package co.astrnt.surveyapp.feature.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import co.astrnt.managersdk.core.MyObserver;
import co.astrnt.managersdk.dao.AddJobApiDao;
import co.astrnt.managersdk.dao.ApiKeyInfoApiDao;
import co.astrnt.managersdk.dao.IndustryApiDao;
import co.astrnt.managersdk.dao.JobTypeApiDao;
import co.astrnt.managersdk.dao.ListIndustryApiDao;
import co.astrnt.managersdk.dao.ListJobTypeApiDao;
import co.astrnt.managersdk.repository.JobRepository;
import co.astrnt.surveyapp.BuildConfig;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;
import co.astrnt.surveyapp.feature.manager.adapter.IndustryAdapter;
import co.astrnt.surveyapp.feature.manager.adapter.JobTypeAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class CreateJobActivity extends BaseActivity {

    private JobRepository mJobRepository;
    private TextView txtCompanyName;
    private EditText inpJobTitle, inpCompanyName, inpLocation,
            inpLogoURL, inpDeadline, inpDescription,
            inpResponsibility, inpRequirements;
    private AppCompatSpinner spnJobType, spnIndustryType;
    private RadioGroup rgStatus;
    private AppCompatCheckBox cbxRequireCV;
    private ProgressDialog progressDialog;

    private List<IndustryApiDao> industries = new ArrayList<>();
    private List<JobTypeApiDao> jobTypes = new ArrayList<>();
    private IndustryAdapter industryAdapter;
    private JobTypeAdapter jobTypeAdapter;
    private IndustryApiDao selectedIndustry;
    private JobTypeApiDao selectedJobType;

    private String jobTitle, companyName, location, logoURL,
            deadline, description, responsibility, requirements,
            jobType, industryType, jobStatus, requireCV;

    public static void start(Context context) {
        Intent intent = new Intent(context, CreateJobActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Create Job");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setContentView(R.layout.activity_create_job);

        txtCompanyName = findViewById(R.id.txt_company_name);
        inpJobTitle = findViewById(R.id.inp_job_title);
        inpCompanyName = findViewById(R.id.inp_company_name);
        inpLocation = findViewById(R.id.inp_location);
        inpLogoURL = findViewById(R.id.inp_logo_url);
        inpDeadline = findViewById(R.id.inp_deadline);
        inpDescription = findViewById(R.id.inp_description);
        inpResponsibility = findViewById(R.id.inp_responsibility);
        inpRequirements = findViewById(R.id.inp_requirement);
        spnJobType = findViewById(R.id.spn_job_type);
        spnIndustryType = findViewById(R.id.spn_industy_type);
        rgStatus = findViewById(R.id.rg_status);
        cbxRequireCV = findViewById(R.id.cbx_require_cv);
        Button btnSubmit = findViewById(R.id.btn_submit);

        mJobRepository = new JobRepository(getManagerApi());

        industryAdapter = new IndustryAdapter(context, android.R.layout.simple_spinner_dropdown_item, industries);
        spnIndustryType.setAdapter(industryAdapter);
        spnIndustryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedIndustry = industryAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        jobTypeAdapter = new JobTypeAdapter(context, android.R.layout.simple_spinner_dropdown_item, jobTypes);
        spnJobType.setAdapter(jobTypeAdapter);
        spnJobType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedJobType = jobTypeAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (BuildConfig.DEBUG) {
            generateDummyData();
        }

        getCompanyInfo();

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateInput();
            }
        });
    }

    private void generateDummyData() {
        Random w = new Random();
        int q = w.nextInt(30000);
        inpJobTitle.setText(q + " Job Title");
        inpCompanyName.setText(q + " Company Name");
        inpLocation.setText(q + " Location");

        Calendar c = new GregorianCalendar();
        c.add(Calendar.DATE, 30);
        Date curDate = c.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = format.format(curDate);

        inpDeadline.setText(dateStr);

        inpDescription.setText(q + " Description");
        inpResponsibility.setText(q + " Responsibility");
        inpRequirements.setText(q + " Requirements");
    }

    private void getCompanyInfo() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mJobRepository.getCompanyInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<ApiKeyInfoApiDao>() {

                    @Override
                    public void onApiResultCompleted() {
                        progressDialog.dismiss();
                        getIndustriesData();
                    }

                    @Override
                    public void onApiResultError(String message, String code) {
                        Timber.e(message);
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onApiResultOk(ApiKeyInfoApiDao apiDao) {
                        Timber.d(apiDao.getMessage());
                        txtCompanyName.setText(apiDao.getCompany_info().getName());
                    }
                });
    }

    private void getIndustriesData() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mJobRepository.getListIndustry()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<ListIndustryApiDao>() {

                    @Override
                    public void onApiResultCompleted() {
                        progressDialog.dismiss();
                        getJobTypeData();
                    }

                    @Override
                    public void onApiResultError(String message, String code) {
                        Timber.e(message);
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onApiResultOk(ListIndustryApiDao apiDao) {
                        Timber.d(apiDao.getMessage());
                        industries = apiDao.getData();
                        industryAdapter.setData(industries);
                    }
                });
    }

    private void getJobTypeData() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mJobRepository.getJobTypes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<ListJobTypeApiDao>() {

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
                    public void onApiResultOk(ListJobTypeApiDao apiDao) {
                        Timber.d(apiDao.getMessage());
                        jobTypes = apiDao.getData();
                        jobTypeAdapter.setData(jobTypes);
                    }
                });
    }

    private void validateInput() {

//        TODO: deadline with date time picker

        jobTitle = inpJobTitle.getText().toString();
        companyName = inpCompanyName.getText().toString();
        location = inpLocation.getText().toString();
        logoURL = inpLogoURL.getText().toString();
        deadline = inpDeadline.getText().toString();
        description = inpDescription.getText().toString();
        responsibility = inpResponsibility.getText().toString();
        requirements = inpRequirements.getText().toString();
        jobType = String.valueOf(selectedJobType.getId());
        industryType = String.valueOf(selectedIndustry.getId());

        requireCV = "0";
        if (cbxRequireCV.isChecked()) {
            requireCV = "2";
        }

        int selectedId = rgStatus.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        jobStatus = radioButton.getText().toString();

        if (TextUtils.isEmpty(jobTitle)) {
            inpJobTitle.setError("Job Title is required");
            inpJobTitle.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(companyName)) {
            inpCompanyName.setError("Company Name is required");
            inpCompanyName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(location)) {
            inpLocation.setError("Location is required");
            inpLocation.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(deadline)) {
            inpDeadline.setError("Deadline is required");
            inpDeadline.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(description)) {
            inpDescription.setError("Deadline is required");
            inpDescription.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(responsibility)) {
            inpResponsibility.setError("Responsibility is required");
            inpResponsibility.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(requirements)) {
            inpRequirements.setError("Requirements is required");
            inpRequirements.requestFocus();
            return;
        }

        createJob();
    }

    private void createJob() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mJobRepository.createJob(jobTitle, jobType, jobStatus, requireCV, description, responsibility,
                requirements, location, industryType, "", deadline, logoURL, companyName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<AddJobApiDao>() {
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
                    public void onApiResultOk(AddJobApiDao apiDao) {
                        Timber.d(apiDao.getMessage());
                        Toast.makeText(context,
                                "Success create Job, job id : " + apiDao.getJob_identifier(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
