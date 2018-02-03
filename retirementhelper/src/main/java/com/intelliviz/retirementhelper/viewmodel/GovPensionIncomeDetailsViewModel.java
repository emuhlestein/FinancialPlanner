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
            List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
            if(gpeList == null) {
                return null;
            }
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            SocialSecurityRules.setRulesOnGovPensionEntities(gpeList, roe);
            if(gpeList.size() == 1) {
                return gpeList.get(0);
            } else {
                if(gpeList.get(0).getId() == params[0]) {
                    return gpeList.get(0);
                } else {
                    return gpeList.get(1);
                }
            }
        }

        @Override
        protected void onPostExecute(GovPensionEntity gpe) {
            mGPE.setValue(gpe);
        }
    }

    private class GetBenefitDataListByIdAsyncTask extends AsyncTask<Long, Void, List<BenefitData>> {

        @Override
        protected List<BenefitData> doInBackground(Long... params) {
            List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
            if(gpeList == null) {
                return null;
            }
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            SocialSecurityRules.setRulesOnGovPensionEntities(gpeList, roe);
            if(gpeList.size() == 1) {
                return gpeList.get(0).getBenefitData();
            } else {
                if(gpeList.get(0).getId() == params[0]) {
                    return gpeList.get(0).getBenefitData();
                } else {
                    return gpeList.get(1).getBenefitData();
                }
            }
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

    private class UpdateAsyncTask extends AsyncTask<GovPensionEntity, Void, List<GovPensionEntity>> {

        @Override
        protected List<GovPensionEntity> doInBackground(GovPensionEntity... params) {
            GovPensionEntity entity = params[0];
            mDB.govPensionDao().update(entity);

            List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            SocialSecurityRules.setRulesOnGovPensionEntities(gpeList, roe);
            return gpeList;
        }

        @Override
        protected void onPostExecute(List<GovPensionEntity> gpeList) {

            if(gpeList.size() == 1) {
                mGPE.setValue(gpeList.get(0));
                List<BenefitData> benefitDataList = gpeList.get(0).getBenefitData();
                if(benefitDataList != null) {
                    mBenefitDataList.setValue(gpeList.get(0).getBenefitData());
                }
            } else if(gpeList.size() == 2) {
                if(gpeList.get(0).getId() == mIncomeId) {
                    mGPE.setValue(gpeList.get(0));
                    List<BenefitData> benefitDataList = gpeList.get(0).getBenefitData();
                    if(benefitDataList != null) {
                        mBenefitDataList.setValue(gpeList.get(0).getBenefitData());
                    }
                } else {
                    mGPE.setValue(gpeList.get(1));
                    List<BenefitData> benefitDataList = gpeList.get(1).getBenefitData();
                    if(benefitDataList != null) {
                        mBenefitDataList.setValue(gpeList.get(1).getBenefitData());
                    }
                }
            }
        }
    }

    private List<BenefitData> getBenefitData(long id) {
        List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
        if(gpeList == null || gpeList.isEmpty()) {
            return Collections.emptyList();
        }
        RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
        SocialSecurityRules.setRulesOnGovPensionEntities(gpeList, roe);
        return getBenefitData(gpeList, id);
    }

    private List<BenefitData> getBenefitData(List<GovPensionEntity> gpeList, long id) {
        GovPensionEntity gpe;
        if(gpeList.size() == 1) {
            gpe = gpeList.get(0);
            return gpe.getBenefitData();
        } else {
            gpe = gpeList.get(0);
            if(gpe.getId() == id) {
                return gpe.getBenefitData();
            } else {
                gpe = gpeList.get(1);
                return gpe.getBenefitData();
            }
        }
    }
}
