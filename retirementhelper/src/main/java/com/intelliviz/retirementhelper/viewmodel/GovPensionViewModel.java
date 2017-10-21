package com.intelliviz.retirementhelper.viewmodel;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.SocialSecurityRules;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.List;

/**
 * Created by edm on 9/26/2017.
 */

public class GovPensionViewModel extends AndroidViewModel {
    private MutableLiveData<GovPensionEntity> mGPID =
            new MutableLiveData<>();
    private AppDatabase mDB;

    public GovPensionViewModel(Application application, long incomeId) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetAsyncTask().execute(incomeId);
    }

    public LiveData<GovPensionEntity> getData() {
        return mGPID;
    }

    public LiveData<List<GovPensionEntity>> getList() {
        return null;
    }

    public void setData(GovPensionEntity gpid) {
        mGPID.setValue(gpid);
        if(gpid.getId() == 0) {
            new InsertAsyncTask().execute(gpid);
        } else {
            new UpdateAsyncTask().execute(gpid);
        }
    }

    public void delete(GovPensionEntity gpid) {
        new DeleteAsyncTask().execute(gpid);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;
        private long mIncomeId;

        public Factory(@NonNull Application application, long incomeId) {
            mApplication = application;
            mIncomeId = incomeId;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new GovPensionViewModel(mApplication, mIncomeId);
        }
    }

    private class GetAsyncTask extends AsyncTask<Long, Void, GovPensionEntity> {

        @Override
        protected GovPensionEntity doInBackground(Long... params) {
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            String birthdate = roe.getBirthdate();
            AgeData endAge = SystemUtils.parseAgeString(roe.getEndAge());
            GovPensionEntity gpe = mDB.govPensionDao().get(params[0]);
            SocialSecurityRules ssr = new SocialSecurityRules(birthdate, endAge, Double.parseDouble(gpe.getFullMonthlyBenefit()));
            gpe.setRules(ssr);
            return gpe;
        }

        @Override
        protected void onPostExecute(GovPensionEntity gpid) {
            mGPID.setValue(gpid);
        }
    }

    private class UpdateAsyncTask extends AsyncTask<GovPensionEntity, Void, Integer> {

        @Override
        protected Integer doInBackground(GovPensionEntity... params) {
            return mDB.govPensionDao().update(params[0]);
        }

        @Override
        protected void onPostExecute(Integer numRowsUpdated) {
        }
    }

    private class InsertAsyncTask extends AsyncTask<GovPensionEntity, Void, Long> {

        @Override
        protected Long doInBackground(GovPensionEntity... params) {
            return mDB.govPensionDao().insert(params[0]);
        }

        @Override
        protected void onPostExecute(Long numRowsInserted) {
        }
    }

    private class DeleteAsyncTask extends AsyncTask<GovPensionEntity, Void, Void> {

        @Override
        protected Void doInBackground(GovPensionEntity... params) {
            mDB.govPensionDao().delete(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
