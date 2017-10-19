package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.retirementhelper.data.MilestoneData;
import com.intelliviz.retirementhelper.data.SocialSecurityRules;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.IncomeSourceEntityBase;
import com.intelliviz.retirementhelper.db.entity.MilestoneAgeEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.util.DataBaseUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 10/16/2017.
 */

public class GovPensionDetailsViewModel extends AndroidViewModel {
    private MutableLiveData<GovPensionEntity> mGPID =
            new MutableLiveData<>();
    private AppDatabase mDB;
    private MutableLiveData<List<MilestoneData>> mMilestones = new MutableLiveData<List<MilestoneData>>();

    public GovPensionDetailsViewModel(Application application, long incomeId) {
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
            return (T) new GovPensionDetailsViewModel(mApplication, mIncomeId);
        }
    }

    private class GetAsyncTask extends AsyncTask<Long, Void, List<MilestoneData>> {

        @Override
        protected List<MilestoneData> doInBackground(Long... params) {
            List<MilestoneAgeEntity> ages = DataBaseUtils.getMilestoneAges(mDB);
            RetirementOptionsEntity rod = mDB.retirementOptionsDao().get();
            GovPensionEntity entity = mDB.govPensionDao().get(params[0]);

            String birthdate = rod.getBirthdate();
            SocialSecurityRules ssr = new SocialSecurityRules(birthdate, Double.parseDouble(entity.getFullMonthlyBenefit()));
            entity.setRules(ssr);

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
