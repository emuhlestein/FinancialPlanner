package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.intelliviz.retirementhelper.data.BenefitData;
import com.intelliviz.retirementhelper.data.SocialSecurityRules;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;

import java.util.Collections;
import java.util.List;

/**
 * Created by Ed Muhlestein on 10/16/2017.
 */

public class GovPensionIncomeDetailsViewModel extends AndroidViewModel {
    private MutableLiveData<GovPensionEntity> mGPE =
            new MutableLiveData<>();
    private AppDatabase mDB;
    private long mIncomeId;
    private MutableLiveData<List<BenefitData>> mBenefitDataList = new MutableLiveData<List<BenefitData>>();

    public GovPensionIncomeDetailsViewModel(Application application, long incomeId) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetAsyncTask().execute(incomeId);
        mIncomeId = incomeId;
    }

    public MutableLiveData<List<BenefitData>> getList() {
        return mBenefitDataList;
    }

    public MutableLiveData<GovPensionEntity> get() {
        return mGPE;
    }

    public void setData(GovPensionEntity gpe) {
        new GetBenefitDataListAsyncTask().execute(gpe);
        mGPE.setValue(gpe);
        new UpdateAsyncTask().execute(gpe);
    }

    public void update() {
        new GetBenefitDataListByIdAsyncTask().execute(mIncomeId);
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

    private class GetAsyncTask extends AsyncTask<Long, Void, GovPensionEntity> {

        @Override
        protected GovPensionEntity doInBackground(Long... params) {
            GovPensionEntity gpe = mDB.govPensionDao().get(params[0]);
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            String birthdate;
            if(gpe.getSpouse() == 1) {
                birthdate = gpe.getSpouseBirhtdate();
            } else {
                birthdate = roe.getBirthdate();
            }
            SocialSecurityRules ssr = new SocialSecurityRules(birthdate, roe.getEndAge(), 0, null);
            gpe.setRules(ssr);
            return gpe;
        }

        @Override
        protected void onPostExecute(GovPensionEntity gpe) {
            mGPE.setValue(gpe);
        }
    }

    private class GetBenefitDataListByIdAsyncTask extends AsyncTask<Long, Void, List<BenefitData>> {

        @Override
        protected List<BenefitData> doInBackground(Long... params) {
            long id = params[0];
            return getBenefitData(id);
        }

        @Override
        protected void onPostExecute(List<BenefitData> benefitDataList) {
            mBenefitDataList.setValue(benefitDataList);
        }
    }

    private class GetBenefitDataListAsyncTask extends AsyncTask<GovPensionEntity, Void, List<BenefitData>> {

        @Override
        protected List<BenefitData> doInBackground(GovPensionEntity... params) {
            GovPensionEntity pie = params[0];
            long id = pie.getId();
            if(id > 0) {
                return getBenefitData(id);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<BenefitData> benefitData) {
            if(benefitData != null) {
                mBenefitDataList.setValue(benefitData);
            }
        }
    }

    private class UpdateAsyncTask extends AsyncTask<GovPensionEntity, Void, Integer> {

        @Override
        protected Integer doInBackground(GovPensionEntity... params) {
            GovPensionEntity entity = params[0];

            return mDB.govPensionDao().update(entity);
        }

        @Override
        protected void onPostExecute(Integer numRowsUpdated) {
        }
    }

    private List<BenefitData> getBenefitData(long id) {
        List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
        RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
        SocialSecurityRules.setRulesOnGovPensionEntities(gpeList, roe);
        GovPensionEntity gpe;
        if(gpeList.get(0).getId() == id) {
            gpe = gpeList.get(0);
            return gpe.getBenefitData();
        } else if(gpeList.get(1).getId() == id) {
            gpe = gpeList.get(1);
            return gpe.getBenefitData();
        } else {
            return Collections.emptyList();
        }
    }
}
