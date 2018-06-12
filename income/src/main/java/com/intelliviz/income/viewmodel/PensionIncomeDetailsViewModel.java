package com.intelliviz.income.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.income.data.AgeData;
import com.intelliviz.income.data.IncomeData;
import com.intelliviz.income.data.PensionRules;
import com.intelliviz.income.db.AppDatabase;
import com.intelliviz.income.db.entity.PensionIncomeEntity;
import com.intelliviz.income.db.entity.RetirementOptionsEntity;

import java.util.List;


/**
 * Created by edm on 11/21/2017.
 */

public class PensionIncomeDetailsViewModel extends AndroidViewModel {
    private MutableLiveData<PensionIncomeEntity> mPIE =
            new MutableLiveData<>();
    private AppDatabase mDB;
    private long mIncomeId;
    private MutableLiveData<List<IncomeData>> mBenefitDataList = new MutableLiveData<List<IncomeData>>();

    public PensionIncomeDetailsViewModel(Application application, long incomeId) {
        super(application);
        mIncomeId = incomeId;
        mDB = AppDatabase.getInstance(application);
        new GetAsyncTask().execute(incomeId);
        new GetBenefitDataListByIdAsyncTask().execute(incomeId);
    }

    public MutableLiveData<List<IncomeData>> getList() {
        return mBenefitDataList;
    }

    public MutableLiveData<PensionIncomeEntity> get() {
        return mPIE;
    }

    public void setData(PensionIncomeEntity pie) {
        new GetBenefitDataListAsyncTask().execute(pie);
        mPIE.setValue(pie);
        new UpdateAsyncTask().execute(pie);
    }

    public void update() {
        new GetBenefitDataListByIdAsyncTask().execute(mIncomeId);
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
            return (T) new PensionIncomeDetailsViewModel(mApplication, mIncomeId);
        }
    }
    private class GetAsyncTask extends AsyncTask<Long, Void, PensionIncomeEntity> {

        public GetAsyncTask() {
        }

        @Override
        protected PensionIncomeEntity doInBackground(Long... params) {
            return mDB.pensionIncomeDao().get(params[0]);
        }

        @Override
        protected void onPostExecute(PensionIncomeEntity pie) {
            mPIE.setValue(pie);
        }
    }

    private class GetBenefitDataListByIdAsyncTask extends AsyncTask<Long, Void, List<IncomeData>> {

        @Override
        protected List<IncomeData> doInBackground(Long... params) {
            long id = params[0];
            return getBenefitData(id);
        }

        @Override
        protected void onPostExecute(List<IncomeData> benefitDataList) {
            mBenefitDataList.setValue(benefitDataList);
        }
    }

    private class GetBenefitDataListAsyncTask extends AsyncTask<PensionIncomeEntity, Void, List<IncomeData>> {

        @Override
        protected List<IncomeData> doInBackground(PensionIncomeEntity... params) {
            PensionIncomeEntity pie = params[0];
            long id = pie.getId();
            if(id > 0) {
                return getBenefitData(id);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<IncomeData> benefitData) {
            if(benefitData != null) {
                mBenefitDataList.setValue(benefitData);
            }
        }
    }

    private class UpdateAsyncTask extends AsyncTask<PensionIncomeEntity, Void, Integer> {

        @Override
        protected Integer doInBackground(PensionIncomeEntity... params) {
            PensionIncomeEntity entity = params[0];

            return mDB.pensionIncomeDao().update(entity);
        }

        @Override
        protected void onPostExecute(Integer numRowsUpdated) {
        }
    }

    private List<IncomeData> getBenefitData(long id) {
        PensionIncomeEntity entity = mDB.pensionIncomeDao().get(id);
        RetirementOptionsEntity rod = mDB.retirementOptionsDao().get();
        String birthdate = rod.getBirthdate();
        AgeData endAge = rod.getEndAge();
        AgeData minAge = entity.getMinAge();
        String monthlyBenefit = entity.getMonthlyBenefit();

        PensionRules rules = new PensionRules(birthdate, minAge, endAge, Double.parseDouble(monthlyBenefit));
        entity.setRules(rules);

        return entity.getIncomeData();
    }
}
