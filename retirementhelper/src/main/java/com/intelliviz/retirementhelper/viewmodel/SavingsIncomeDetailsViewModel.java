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
import com.intelliviz.retirementhelper.data.IncomeDataAccessor;
import com.intelliviz.retirementhelper.data.IncomeDetails;
import com.intelliviz.retirementhelper.data.Savings401kIncomeRules;
import com.intelliviz.retirementhelper.data.SavingsIncomeRules;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity;
import com.intelliviz.retirementhelper.util.AgeUtils;
import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 10/23/2017.
 */

public class SavingsIncomeDetailsViewModel extends AndroidViewModel {
    private AppDatabase mDB;
    private MutableLiveData<SavingsIncomeEntity> mSIE =
            new MutableLiveData<>();
    private MutableLiveData<List<IncomeDetails>> mIncomeDetails = new MutableLiveData<List<IncomeDetails>>();
    private long mIncomeId;

    public SavingsIncomeDetailsViewModel(Application application, long incomeId) {
        super(application);
        mIncomeId = incomeId;
        mDB = AppDatabase.getInstance(application);
        new GetAsyncTask().execute(incomeId);
        new GetBenefitDataListByIdAsyncTask().execute(incomeId);
    }

    public MutableLiveData<List<IncomeDetails>> getList() {
        return mIncomeDetails;
    }

    public MutableLiveData<SavingsIncomeEntity> get() {
        return mSIE;
    }

    public void update() {
        new GetBenefitDataListByIdAsyncTask().execute(mIncomeId);
    }

    public void setData(SavingsIncomeEntity sie) {
        new GetBenefitDataListAsyncTask().execute(sie);
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

    private class GetBenefitDataListByIdAsyncTask extends AsyncTask<Long, Void, List<IncomeDetails>> {

        @Override
        protected List<IncomeDetails> doInBackground(Long... params) {
            long id = params[0];
            return getIncomeDetails(id);
        }

        @Override
        protected void onPostExecute(List<IncomeDetails> incomeDetails) {
            mIncomeDetails.setValue(incomeDetails);
        }
    }

    private class GetBenefitDataListAsyncTask extends AsyncTask<SavingsIncomeEntity, Void, List<IncomeDetails>> {

        @Override
        protected List<IncomeDetails> doInBackground(SavingsIncomeEntity... params) {
            SavingsIncomeEntity sie = params[0];
            long id = sie.getId();
            if(id > 0) {
                return getIncomeDetails(id);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<IncomeDetails> benefitData) {
            if(benefitData != null) {
                mIncomeDetails.setValue(benefitData);
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

    private List<IncomeDetails> getIncomeDetails(long id) {
        RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
        SavingsIncomeEntity entity = mDB.savingsIncomeDao().get(id);
        String birthdate = roe.getBirthdate();
        AgeData endAge = roe.getEndAge();
        if(entity.getType() == RetirementConstants.INCOME_TYPE_401K) {
            Savings401kIncomeRules s4ir = new Savings401kIncomeRules(birthdate, endAge);
            entity.setRules(s4ir);
        } else {
            SavingsIncomeRules sir = new SavingsIncomeRules(birthdate, endAge);
            entity.setRules(sir);

        }

        AgeData startAge = AgeUtils.getAge(roe.getBirthdate());
        endAge = roe.getEndAge();
        IncomeDataAccessor accessor = entity.getIncomeDataAccessor();
        List<IncomeDetails> incomeDetails = new ArrayList<>();
        for(int year = startAge.getYear(); year <= endAge.getYear(); year++) {
            AgeData age = new AgeData(year, 0);
            BenefitData benefitData = accessor.getBenefitData(age);
            String line1;
            int status;
            String balance;
            String amount;
            if(benefitData == null) {
                balance = "0.0";
                amount = "0.0";
                status = 0;
            } else {
                balance = SystemUtils.getFormattedCurrency(benefitData.getBalance());
                amount = SystemUtils.getFormattedCurrency(benefitData.getMonthlyAmount());
                status = benefitData.getBalanceState();
                if (benefitData.isPenalty()) {
                    //status = 0;
                }
            }
            line1 = age.toString() + "   " + amount + "  " + balance;
            IncomeDetails incomeDetail = new IncomeDetails(line1, status, "");
            incomeDetails.add(incomeDetail);
        }

        return incomeDetails;
    }
}
