package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.PensionData;
import com.intelliviz.retirementhelper.data.PensionRules;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.MilestoneAgeEntity;
import com.intelliviz.retirementhelper.db.entity.PensionIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 11/21/2017.
 */

public class PensionIncomeDetailsViewModel extends AndroidViewModel {
    private MutableLiveData<PensionData> mPID =
            new MutableLiveData<>();
    private AppDatabase mDB;
    private MutableLiveData<List<PensionData>> mPensionData = new MutableLiveData<List<PensionData>>();

    public PensionIncomeDetailsViewModel(Application application, long incomeId) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new PensionIncomeDetailsViewModel.GetAsyncTask().execute(incomeId);
    }

    public MutableLiveData<List<PensionData>> get() {
        return mPensionData;
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

    private class GetAsyncTask extends AsyncTask<Long, Void, List<PensionData>> {

        @Override
        protected List<PensionData> doInBackground(Long... params) {
            List<MilestoneAgeEntity> ages = DataBaseUtils.getMilestoneAges(mDB);
            RetirementOptionsEntity rod = mDB.retirementOptionsDao().get();
            PensionIncomeEntity entity = mDB.pensionIncomeDao().get(params[0]);

            String birthdate = rod.getBirthdate();
            AgeData minAge = SystemUtils.parseAgeString(entity.getMinAge());
            AgeData endAge = SystemUtils.parseAgeString(rod.getEndAge());

            PensionRules pr = new PensionRules(minAge, endAge,  Double.parseDouble(entity.getMonthlyBenefit()));
            entity.setRules(pr);

            List<PensionData> listPensionData = new ArrayList<>();
            for(MilestoneAgeEntity age : ages) {
                PensionData data = entity.getMonthlyBenefitForAge(age.getAge());
                if(data != null) {
                    listPensionData.add(data);
                }
            }

            return listPensionData;
        }

        @Override
        protected void onPostExecute(List<PensionData> entities) {
            mPensionData.setValue(entities);
        }
    }
}
