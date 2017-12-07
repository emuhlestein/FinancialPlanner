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
import com.intelliviz.retirementhelper.data.TaxDeferredIncomeRules;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.MilestoneAgeEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.db.entity.TaxDeferredIncomeEntity;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.List;

/**
 * Created by edm on 10/23/2017.
 */

public class TaxDeferredDetailsViewModel extends AndroidViewModel {
    private AppDatabase mDB;
    private MutableLiveData<TaxDeferredIncomeEntity> mTDID =
            new MutableLiveData<>();
    private MutableLiveData<List<AmountData>> mListTaxDeferredData = new MutableLiveData<List<AmountData>>();
    private long mIncomeId;

    public TaxDeferredDetailsViewModel(Application application, long incomeId) {
        super(application);
        mIncomeId = incomeId;
        mDB = AppDatabase.getInstance(application);
        new GetAsyncTask().execute(incomeId);
        new GetTaxDeferredDataAsyncTask().execute(incomeId);
    }

    public MutableLiveData<List<AmountData>> getList() {
        return mListTaxDeferredData;
    }

    public MutableLiveData<TaxDeferredIncomeEntity> get() {
        return mTDID;
    }

    public void update() {
        new GetTaxDeferredDataAsyncTask().execute(mIncomeId);
    }

    public void setData(TaxDeferredIncomeEntity tdid) {
        mTDID.setValue(tdid);
        new TaxDeferredDetailsViewModel.UpdateAsyncTask().execute(tdid);
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
            return (T) new TaxDeferredDetailsViewModel(mApplication, mIncomeId);
        }
    }

    private class GetAsyncTask extends AsyncTask<Long, Void, TaxDeferredIncomeEntity> {

        public GetAsyncTask() {
        }

        @Override
        protected TaxDeferredIncomeEntity doInBackground(Long... params) {
            return mDB.taxDeferredIncomeDao().get(params[0]);
        }

        @Override
        protected void onPostExecute(TaxDeferredIncomeEntity tdid) {
            mTDID.setValue(tdid);
        }
    }

    private class GetTaxDeferredDataAsyncTask extends AsyncTask<Long, Void, List<AmountData>> {

        @Override
        protected List<AmountData> doInBackground(Long... params) {
            TaxDeferredIncomeEntity tdid = mDB.taxDeferredIncomeDao().get(params[0]);
            List<MilestoneAgeEntity> ages = DataBaseUtils.getMilestoneAges(mDB);
            RetirementOptionsEntity rod = mDB.retirementOptionsDao().get();
            TaxDeferredIncomeEntity entity = mDB.taxDeferredIncomeDao().get(params[0]);
            String birthdate = rod.getBirthdate();
            AgeData endAge = SystemUtils.parseAgeString(rod.getEndAge());
            AgeData startAge = SystemUtils.parseAgeString(tdid.getStartAge());
            TaxDeferredIncomeRules tdir = new TaxDeferredIncomeRules(birthdate, endAge, startAge,
                    Double.parseDouble(entity.getBalance()),
                    Double.parseDouble(entity.getInterest()),
                    Double.parseDouble(entity.getMonthlyIncrease()),
                    rod.getWithdrawMode(), Double.parseDouble(rod.getWithdrawAmount()));
            entity.setRules(tdir);

            return entity.getMonthlyAmountData();

//            List<TaxDeferredData> listTaxDeferredData = new ArrayList<>();
            //TaxDeferredData data = entity.getMonthlyBenefitForAge(age);
/*

            for(MilestoneAgeEntity age : ages) {
                TaxDeferredData data = entity.getMonthlyBenefitForAge(age.getAge());
                if(data != null) {
                    listTaxDeferredData.add(data);
                }
            }
*/

//            return listTaxDeferredData;
        }

        @Override
        protected void onPostExecute(List<AmountData> milestones) {
            mListTaxDeferredData.setValue(milestones);
        }
    }

    private class UpdateAsyncTask extends AsyncTask<TaxDeferredIncomeEntity, Void, Integer> {

        @Override
        protected Integer doInBackground(TaxDeferredIncomeEntity... params) {
            return mDB.taxDeferredIncomeDao().update(params[0]);
        }

        @Override
        protected void onPostExecute(Integer numRowsUpdated) {
        }
    }
}
