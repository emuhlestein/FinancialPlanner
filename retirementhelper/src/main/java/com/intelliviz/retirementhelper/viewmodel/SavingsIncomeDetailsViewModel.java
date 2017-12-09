package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.AmountData;
import com.intelliviz.retirementhelper.data.Savings401kIncomeRules;
import com.intelliviz.retirementhelper.data.SavingsIncomeRules;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.List;

/**
 * Created by edm on 10/23/2017.
 */

public class SavingsIncomeDetailsViewModel extends AndroidViewModel {
    private AppDatabase mDB;
    private MutableLiveData<SavingsIncomeEntity> mSIE =
            new MutableLiveData<>();
    private MutableLiveData<List<AmountData>> mListTaxDeferredData = new MutableLiveData<List<AmountData>>();
    private long mIncomeId;

    public SavingsIncomeDetailsViewModel(Application application, long incomeId) {
        super(application);
        mIncomeId = incomeId;
        mDB = AppDatabase.getInstance(application);
        new GetAsyncTask().execute(incomeId);
        new GetTaxDeferredDataAsyncTask().execute(incomeId);
    }

    public MutableLiveData<List<AmountData>> getList() {
        return mListTaxDeferredData;
    }

    public MutableLiveData<SavingsIncomeEntity> get() {
        return mSIE;
    }

    public void update() {
        new GetTaxDeferredDataAsyncTask().execute(mIncomeId);
    }

    public void setData(SavingsIncomeEntity tdid) {
        mSIE.setValue(tdid);
        new SavingsIncomeDetailsViewModel.UpdateAsyncTask().execute(tdid);
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

    private class GetTaxDeferredDataAsyncTask extends AsyncTask<Long, Void, List<AmountData>> {

        @Override
        protected List<AmountData> doInBackground(Long... params) {
            SavingsIncomeEntity sie = mDB.savingsIncomeDao().get(params[0]);
            RetirementOptionsEntity rod = mDB.retirementOptionsDao().get();
            SavingsIncomeEntity entity = mDB.savingsIncomeDao().get(params[0]);
            String birthdate = rod.getBirthdate();
            AgeData endAge = SystemUtils.parseAgeString(rod.getEndAge());
            AgeData startAge = SystemUtils.parseAgeString(sie.getStartAge());
            if(sie.getType() == RetirementConstants.INCOME_TYPE_401K) {
                Savings401kIncomeRules tdir = new Savings401kIncomeRules(birthdate, endAge, startAge,
                        Double.parseDouble(entity.getBalance()),
                        Double.parseDouble(entity.getInterest()),
                        Double.parseDouble(entity.getMonthlyAddition()),
                        rod.getWithdrawMode(), Double.parseDouble(rod.getWithdrawAmount()));
                entity.setRules(tdir);
            } else {
                SavingsIncomeRules sir = new SavingsIncomeRules(birthdate, endAge, startAge,
                        Double.parseDouble(entity.getBalance()),
                        Double.parseDouble(entity.getInterest()),
                        Double.parseDouble(entity.getMonthlyAddition()),
                        rod.getWithdrawMode(), Double.parseDouble(rod.getWithdrawAmount()));
                entity.setRules(sir);

            }

            return entity.getMonthlyAmountData();
        }

        @Override
        protected void onPostExecute(List<AmountData> milestones) {
            mListTaxDeferredData.setValue(milestones);
        }
    }

    private class UpdateAsyncTask extends AsyncTask<SavingsIncomeEntity, Void, Integer> {

        @Override
        protected Integer doInBackground(SavingsIncomeEntity... params) {
            return mDB.savingsIncomeDao().update(params[0]);
        }

        @Override
        protected void onPostExecute(Integer numRowsUpdated) {
        }
    }
}
