package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;
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
    private MutableLiveData<List<MilestoneData>> mMilestones = new MutableLiveData<List<MilestoneData>>();
    private long mIncomeId;

    public TaxDeferredDetailsViewModel(Application application, long incomeId) {
        super(application);
        mIncomeId = incomeId;
        mDB = AppDatabase.getInstance(application);
        new GetMilestonesAsyncTask().execute(incomeId);
        new GetAsyncTask().execute(incomeId);
    }

    public MutableLiveData<List<MilestoneData>> getMilestones() {
        return mMilestones;
    }

    public MutableLiveData<TaxDeferredIncomeEntity> get() {
        return mTDID;
    }

    public void update() {
        new GetMilestonesAsyncTask().execute(mIncomeId);
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

    private class GetMilestonesAsyncTask extends AsyncTask<Long, Void, List<MilestoneData>> {

        @Override
        protected List<MilestoneData> doInBackground(Long... params) {
            List<MilestoneAgeEntity> ages = DataBaseUtils.getMilestoneAges(mDB);
            RetirementOptionsEntity rod = mDB.retirementOptionsDao().get();
            TaxDeferredIncomeEntity entity = mDB.taxDeferredIncomeDao().get(params[0]);

            String birthdate = rod.getBirthdate();
            AgeData endAge = SystemUtils.parseAgeString(rod.getEndAge());
            TaxDeferredIncomeRules tdir = new TaxDeferredIncomeRules(birthdate, endAge,
                    Double.parseDouble(entity.getBalance()),
                    Double.parseDouble(entity.getInterest()),
                    Double.parseDouble(entity.getMonthlyIncrease()),
                    rod.getWithdrawMode(), Double.parseDouble(rod.getWithdrawAmount()));
            entity.setRules(tdir);

            return entity.getMilestones(ages, rod);
        }

        @Override
        protected void onPostExecute(List<MilestoneData> milestones) {
            mMilestones.setValue(milestones);
        }
    }
}
