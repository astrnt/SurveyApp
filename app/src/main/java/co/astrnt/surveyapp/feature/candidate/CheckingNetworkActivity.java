package co.astrnt.surveyapp.feature.candidate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import co.astrnt.qasdk.core.MyObserver;
import co.astrnt.qasdk.dao.BaseApiDao;
import co.astrnt.qasdk.repository.InterviewRepository;
import co.astrnt.surveyapp.R;
import co.astrnt.surveyapp.base.BaseActivity;
import co.astrnt.surveyapp.model.DeviceErrorDao;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class CheckingNetworkActivity extends BaseActivity {

    private InterviewRepository interviewRepository;
    private boolean isNetworkOk = true;

    public static void start(Context context) {
        Intent intent = new Intent(context, CheckingNetworkActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking_network);

        interviewRepository = new InterviewRepository(getApi());

        pingNetwork();
    }

    public void showNetworkOk(boolean isNetworkOk) {
        this.isNetworkOk = isNetworkOk;
        moveToNext();
    }

    public void showNext() {
        VideoInstructionActivity.start(context);
        finish();
    }

    private void moveToNext() {
        if (isNetworkOk) {
            showNext();
        } else {
            DeviceErrorDao deviceErrorDao = new DeviceErrorDao(true, true, true, isNetworkOk, DeviceErrorDao.TYPE_NETWORK);
            CheckingDeviceErrorActivity.start(context, deviceErrorDao);
            finish();
        }
    }

    public void pingNetwork() {
        final long startTime = System.currentTimeMillis();

        interviewRepository.pingNetwork()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MyObserver<BaseApiDao>() {

                    @Override
                    public void onApiResultCompleted() {
                    }

                    @Override
                    public void onApiResultError(String message, String code) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onApiResultOk(BaseApiDao baseApiDao) {
                        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                        Timber.d("ping time : %s", elapsedTime);

                        isNetworkOk = elapsedTime < 5;

                        showNetworkOk(isNetworkOk);
                    }
                });
    }
}
