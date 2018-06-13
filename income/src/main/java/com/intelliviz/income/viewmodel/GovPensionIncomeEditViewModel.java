package com.intelliviz.income.viewmodel;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.income.R;
import com.intelliviz.income.data.AgeData;
import com.intelliviz.income.data.SocialSecurityRules;
import com.intelliviz.income.db.AppDatabase;
import com.intelliviz.income.db.entity.GovPensionEntity;
import com.intelliviz.income.db.entity.RetirementOptionsEntity;
import com.intelliviz.income.util.AgeUtils;
import com.intelliviz.income.util.GovEntityAccessor;
import com.intelliviz.income.util.RetirementConstants;

import java.util.List;


/**
 * NOTE: Since the view model last for the entire duration of the app, the async tasks
 * don't need to be static nested classes, they can be inner classes.
 *
 * Created by edm on 9/26/2017.
 */

public class GovPensionIncomeEditViewModel extends AndroidViewModel {

    private MutableLiveData<LiveDataWrapper> mGPE =
            new MutableLiveData<>();
    private RetirementOptionsEntity mROE;
    private AppDatabase mDB;
    private long mIncomeId;

    public GovPensionIncomeEditViewModel(Application application, long incomeId) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        mIncomeId = incomeId;
       new GetAsyncTask().execute(mIncomeId);
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

    public LiveData<LiveDataWrapper> get() {
        return mGPE;
    }

    public void setData(GovPensionEntity gpe) {
        if(gpe.getId() == 0) {
            new InsertAsyncTask().execute(gpe);
        } else {
            new UpdateAsyncTask().execute(gpe);
        }
    }

    public void updateSpouseBirthdate(String birthdate) {
        new UpdateSpouseBirthdateAsyncTask().execute(birthdate);
    }

    public void delete(GovPensionEntity gpid) {
        new DeleteAsyncTask().execute(gpid);
    }

    private class GetAsyncTask extends AsyncTask<Long, Void, LiveDataWrapper> {

        @Override
        protected LiveDataWrapper doInBackground(Long... params) {
            long id = params[0];

            String[] errorCodes = getApplication().getResources().getStringArray(R.array.error_codes);

            List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            GovEntityAccessor govEntityAccessor = new GovEntityAccessor(gpeList, roe);
            LiveDataWrapper liveDataWrapper = govEntityAccessor.getEntity(id);

            String message = "";
            if(liveDataWrapper.getState() != 0) {
                message = errorCodes[liveDataWrapper.getState()];
                liveDataWrapper.setMessage(message);
            }
            return liveDataWrapper;
        }

        @Override
        protected void onPostExecute(LiveDataWrapper gpe) {
            mGPE.setValue(gpe);
        }
    }
    private class UpdateSpouseBirthdateAsyncTask extends android.os.AsyncTask<String, Void, RetirementOptionsEntity> {

        @Override
        protected RetirementOptionsEntity doInBackground(String... params) {
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            roe.setIncludeSpouse(1);
            roe.setSpouseBirthdate(params[0]);
            mDB.retirementOptionsDao().update(roe);
            return roe;
        }

        @Override
        protected void onPostExecute(RetirementOptionsEntity roe) {
            GovPensionEntity gpe = (GovPensionEntity) mGPE.getValue().getObj();
            if(gpe == null) {
                int year = AgeUtils.getBirthYear(roe.getSpouseBirthdate());
                AgeData age = SocialSecurityRules.getFullRetirementAgeFromYear(year);
                gpe = new GovPensionEntity(0, RetirementConstants.INCOME_TYPE_GOV_PENSION,
                        "", "0", age, 1);
            }
            gpe.setRules(new SocialSecurityRules(roe.getEndAge(), roe.getSpouseBirthdate()));
            mGPE.setValue(new LiveDataWrapper(gpe, 0, ""));
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
        }
    }
}
