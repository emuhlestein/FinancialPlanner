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
import com.intelliviz.retirementhelper.data.PensionData;
import com.intelliviz.retirementhelper.data.PensionRules;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.MilestoneAgeEntity;
import com.intelliviz.retirementhelper.db.entity.PensionIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.util.DataBaseUtils;

import java.util.List;

/**
 * Created by edm on 11/21/2017.
 */

public class PensionIncomeDetailsViewModel extends AndroidViewModel {
    private MutableLiveData<PensionData> mPID =
            new MutableLiveData<>();
    private AppDatabase mDB;
    private MutableLiveData<List<BenefitData>> mPensionData = new MutableLiveData<List<BenefitData>>();

    public PensionIncomeDetailsViewModel(Application application, long incomeId) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new PensionIncomeDetailsViewModel.GetAsyncTask().execute(incomeId);
    }

    public MutableLiveData<List<BenefitData>> get() {
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

    private class GetAsyncTask extends AsyncTask<Long, Void, List<BenefitData>> {

        @Override
        protected List<BenefitData> doInBackground(Long... params) {
            List<MilestoneAgeEntity> ages = DataBaseUtils.getMilestoneAges(mDB);
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            PensionIncomeEntity entity = mDB.pensionIncomeDao().get(params[0]);

            AgeData minAge = entity.getMinAge();
            AgeData endAge = roe.getEndAge();

            PensionRules pr = new PensionRules(roe.getBirthdate(), minAge, endAge,  Double.parseDouble(entity.getMonthlyBenefit()));
            entity.setRules(pr);
/*
            List<BenefitData> listBenefitData = new ArrayList<>();
            for(MilestoneAgeEntity age : ages) {
                BenefitData data = entity.getBenefitForAge(age.getAge());
                if(data != null) {
                    listBenefitData.add(data);
                }
            }
*/
            return entity.getBenefitData();
        }

        @Override
        protected void onPostExecute(List<BenefitData> entities) {
            mPensionData.setValue(entities);
        }
    }
}
