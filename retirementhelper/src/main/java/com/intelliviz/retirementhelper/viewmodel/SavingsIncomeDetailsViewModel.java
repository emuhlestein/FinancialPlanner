package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.BenefitData;
import com.intelliviz.retirementhelper.data.Savings401kIncomeRules;
import com.intelliviz.retirementhelper.data.SavingsIncomeRules;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity;
import com.intelliviz.retirementhelper.util.RetirementConstants;

import java.util.List;

/**
 * Created by edm on 10/23/2017.
 */

public class SavingsIncomeDetailsViewModel extends AndroidViewModel {
    private AppDatabase mDB;
    private MutableLiveData<SavingsIncomeEntity> mSIE =
            new MutableLiveData<>();
    private MutableLiveData<List<BenefitData>> mAmountDataList = new MutableLiveData<List<BenefitData>>();
    private long mIncomeId;

    public SavingsIncomeDetailsViewModel(Application application, long incomeId) {
        super(application);
        mIncomeId = incomeId;
        mDB = AppDatabase.getInstance(application);
        new GetAsyncTask().execute(incomeId);
        new GetAmountDataListByIdAsyncTask().execute(incomeId);
    }

    public MutableLiveData<List<BenefitData>> getList() {
        return mAmountDataList;
    }

    public MutableLiveData<SavingsIncomeEntity> get() {
        return mSIE;
    }

    public void update() {
        new GetAmountDataListByIdAsyncTask().execute(mIncomeId);
    }

    public void setData(SavingsIncomeEntity sie) {
        new GetAmountDataListAsyncTask().execute(sie);
        mSIE.setValue(sie);
        new UpdateAsyncTask().execute(sie);
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
            return (T) new SavingsIncomeDetailsViewModel(mApplication, mIncomeId);
        }
    }

    private class GetAsyncTask extends AsyncTask<Long, Void, SavingsIncomeEntity> {

        public GetAsyncTask() {
        }

        @Override
        protected SavingsIncomeEntity doInBackground(Long... params) {
            return mDB.savingsIncomeDao().get(params[0]);
        }

        @Override
        protected void onPostExecute(SavingsIncomeEntity tdid) {
            mSIE.setValue(tdid);
        }
    }

    private class GetAmountDataListByIdAsyncTask extends AsyncTask<Long, Void, List<BenefitData>> {

        @Override
        protected List<BenefitData> doInBackground(Long... params) {
            long id = params[0];
            return getAmountData(id);
        }

        @Override
        protected void onPostExecute(List<BenefitData> benefitDataList) {
            mAmountDataList.setValue(benefitDataList);
        }
    }

    private class GetAmountDataListAsyncTask extends AsyncTask<SavingsIncomeEntity, Void, List<BenefitData>> {

        @Override
        protected List<BenefitData> doInBackground(SavingsIncomeEntity... params) {
            SavingsIncomeEntity sie = params[0];
            long id = sie.getId();
            if(id > 0) {
                return getAmountData(id);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<BenefitData> benefitData) {
            if(benefitData != null) {
                mAmountDataList.setValue(benefitData);
            }
        }
    }

    private class UpdateAsyncTask extends AsyncTask<SavingsIncomeEntity, Void, Integer> {

        @Override
        protected Integer doInBackground(SavingsIncomeEntity... params) {
            SavingsIncomeEntity sie = params[0];

            return mDB.savingsIncomeDao().update(sie);
        }

        @Override
        protected void onPostExecute(Integer numRowsUpdated) {
        }
    }

    private List<BenefitData> getAmountData(long id) {
        SavingsIncomeEntity sie = mDB.savingsIncomeDao().get(id);
        RetirementOptionsEntity rod = mDB.retirementOptionsDao().get();
        SavingsIncomeEntity entity = mDB.savingsIncomeDao().get(id);
        String birthdate = rod.getBirthdate();
        AgeData endAge = rod.getEndAge();
        AgeData startAge = sie.getStartAge();
        if(sie.getType() == RetirementConstants.INCOME_TYPE_401K) {
            Savings401kIncomeRules tdir = new Savings401kIncomeRules(birthdate, startAge, endAge,
                    Double.parseDouble(entity.getBalance()),
                    Double.parseDouble(entity.getInterest()),
                    Double.parseDouble(entity.getMonthlyAddition()),
                    entity.getWithdrawMode(), Double.parseDouble(entity.getWithdrawAmount()));
            entity.setRules(tdir);
        } else {
            SavingsIncomeRules sir = new SavingsIncomeRules(birthdate, startAge, endAge,
                    Double.parseDouble(entity.getBalance()),
                    Double.parseDouble(entity.getInterest()),
                    Double.parseDouble(entity.getMonthlyAddition()),
                    entity.getWithdrawMode(), Double.parseDouble(entity.getWithdrawAmount()));
            entity.setRules(sir);

        }

        return entity.getBenefitData();
    }
}
