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
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.List;

/**
 * Created by edm on 9/26/2017.
 */

public class GovPensionIncomeEditViewModel extends AndroidViewModel {
    private MutableLiveData<GovPensionEntity> mGPID =
            new MutableLiveData<>();
    private RetirementOptionsEntity mROE;
    private MutableLiveData<String> mBirthdate = new MutableLiveData<>();
    private AppDatabase mDB;

    public GovPensionIncomeEditViewModel(Application application, long incomeId) {
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

    public void setData(GovPensionEntity gpe) {
        String birthdate = mROE.getBirthdate();
        AgeData endAge = SystemUtils.parseAgeString(mROE.getEndAge());
        SocialSecurityRules ssr = new SocialSecurityRules(birthdate, endAge);
        gpe.setRules(ssr);
        mGPID.setValue(gpe);
        if(gpe.getId() == 0) {
            new InsertAsyncTask().execute(gpe);
        } else {
            new UpdateAsyncTask().execute(gpe);
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
            return (T) new GovPensionIncomeEditViewModel(mApplication, mIncomeId);
        }
    }

    private class GetAsyncTask extends AsyncTask<Long, Void, GovPensionEntity> {

        @Override
        protected GovPensionEntity doInBackground(Long... params) {
            long id = params[0];
            GovPensionEntity entity;
            mROE = mDB.retirementOptionsDao().get();
            String birthdate = mROE.getBirthdate();
            AgeData startAge = SystemUtils.getAge(birthdate);
            AgeData endAge = SystemUtils.parseAgeString(mROE.getEndAge());
            if(id == 0) {
                // create default entity
                entity = new GovPensionEntity(id, RetirementConstants.INCOME_TYPE_GOV_PENSION,
                        "Social Security", "0", 0, "0",
                        "0", startAge.getUnformattedString() );
            } else {
                entity = mDB.govPensionDao().get(params[0]);
            }

            if(entity != null) {
                SocialSecurityRules ssr = new SocialSecurityRules(birthdate, endAge);
                entity.setRules(ssr);
                return entity;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(GovPensionEntity gpid) {
            if(gpid != null) {
                mGPID.setValue(gpid);
            }
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
