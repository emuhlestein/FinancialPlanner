package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.GovPensionData;
import com.intelliviz.retirementhelper.data.SocialSecurityRules;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.MilestoneAgeEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.util.DataBaseUtils;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 10/16/2017.
 */

public class GovPensionIncomeDetailsViewModel extends AndroidViewModel {
    private MutableLiveData<GovPensionData> mGPID =
            new MutableLiveData<>();
    private AppDatabase mDB;
    private MutableLiveData<List<GovPensionData>> mGovPensionData = new MutableLiveData<List<GovPensionData>>();

    public GovPensionIncomeDetailsViewModel(Application application, long incomeId) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetAsyncTask().execute(incomeId);
    }

    public MutableLiveData<List<GovPensionData>> get() {
        return mGovPensionData;
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
            return (T) new GovPensionIncomeDetailsViewModel(mApplication, mIncomeId);
        }
    }

    private class GetAsyncTask extends AsyncTask<Long, Void, List<GovPensionData>> {

        @Override
        protected List<GovPensionData> doInBackground(Long... params) {
            List<MilestoneAgeEntity> ages = DataBaseUtils.getMilestoneAges(mDB);
            RetirementOptionsEntity rod = mDB.retirementOptionsDao().get();
            GovPensionEntity entity = mDB.govPensionDao().get(params[0]);

            String birthdate = rod.getBirthdate();
            AgeData endAge = SystemUtils.parseAgeString(rod.getEndAge());
            SocialSecurityRules ssr = new SocialSecurityRules(birthdate, endAge,
                    Double.parseDouble(entity.getFullMonthlyBenefit()),
                    entity.getSpouse(),
                    Double.parseDouble(entity.getSpouseBenefit()), entity.getSpouseBirhtdate());
            entity.setRules(ssr);

            List<GovPensionData> listGovPensionData = new ArrayList<>();
            for(MilestoneAgeEntity age : ages) {
                GovPensionData data = entity.getMonthlyBenefitForAge(age.getAge());
                if(data != null) {
                    listGovPensionData.add(data);
                }

            }

            return listGovPensionData;
        }

        @Override
        protected void onPostExecute(List<GovPensionData> entities) {
            mGovPensionData.setValue(entities);
        }
    }
}
