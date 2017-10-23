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
import com.intelliviz.retirementhelper.db.entity.IncomeSourceEntityBase;
import com.intelliviz.retirementhelper.db.entity.MilestoneAgeEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.db.entity.TaxDeferredIncomeEntity;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 10/23/2017.
 */

public class TaxDeferredDetailsViewModel extends AndroidViewModel {
    private AppDatabase mDB;
    private MutableLiveData<List<MilestoneData>> mMilestones = new MutableLiveData<List<MilestoneData>>();

    public TaxDeferredDetailsViewModel(Application application, long incomeId) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetAsyncTask().execute(incomeId);
    }

    public MutableLiveData<List<MilestoneData>> get() {
        return mMilestones;
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

    private class GetAsyncTask extends AsyncTask<Long, Void, List<MilestoneData>> {

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

            List<IncomeSourceEntityBase> list = new ArrayList<>();
            list.add(entity);
            List<MilestoneData> milestones = DataBaseUtils.getAllMilestones(list, ages, rod);
            return milestones;
        }

        @Override
        protected void onPostExecute(List<MilestoneData> milestones) {
            mMilestones.setValue(milestones);
        }
    }
}
