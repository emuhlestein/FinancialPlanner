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
import com.intelliviz.retirementhelper.data.IncomeDetails;
import com.intelliviz.retirementhelper.data.SocialSecurityRules;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ed Muhlestein on 10/16/2017.
 */

public class GovPensionIncomeDetailsViewModel extends AndroidViewModel {
    private MutableLiveData<GovPensionEntity> mGPE =
            new MutableLiveData<>();
    private AppDatabase mDB;
    private long mIncomeId;
    private MutableLiveData<List<IncomeDetails>> mBenefitDataList = new MutableLiveData<List<IncomeDetails>>();

    public GovPensionIncomeDetailsViewModel(Application application, long incomeId) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetAsyncTask().execute(incomeId);
        mIncomeId = incomeId;
    }

    public MutableLiveData<List<IncomeDetails>> getList() {
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

    private class GetBenefitDataListByIdAsyncTask extends AsyncTask<Long, Void, GovPensionEntity> {

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
            mBenefitDataList.setValue(getIncomeDetails(gpe));
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
                    mBenefitDataList.setValue(getIncomeDetails(gpeList.get(0)));
                }
            } else if(gpeList.size() == 2) {
                GovPensionEntity gpe;
                if(gpeList.get(0).getId() == mIncomeId) {
                    gpe = gpeList.get(0);
                } else {
                    gpe = gpeList.get(1);
                }


                List<BenefitData> benefitDataList = gpe.getBenefitData();
                if(benefitDataList != null) {
                    mBenefitDataList.setValue(getIncomeDetails(gpe));
                }
                mGPE.setValue(gpe);
            }
        }
    }

    private List<IncomeDetails> getIncomeDetails(GovPensionEntity gpe) {

        double monthlyBenefit = gpe.getMonthlyBenefit();
        double fullMonthlyBenefit = Double.parseDouble(gpe.getFullMonthlyBenefit());

        String message = "";
        boolean addMessage = false;
        if(monthlyBenefit > fullMonthlyBenefit) {
            addMessage = true;
            message = "spousal benefits apply";
        }

        List<BenefitData> listBenefitData = gpe.getBenefitData();
        List<IncomeDetails> incomeDetails = new ArrayList<>();
        for(BenefitData benefitData : listBenefitData) {
            AgeData age = benefitData.getAge();
            String amount = SystemUtils.getFormattedCurrency(benefitData.getMonthlyAmount());
            String line1 = age.toString() + "   " + amount;
            IncomeDetails incomeDetail;

            if(addMessage) {
                incomeDetail = new IncomeDetails(line1, benefitData.getBalanceState(), message);
                incomeDetail.setAcceptClick(true);
                addMessage = false;
            } else {
                incomeDetail = new IncomeDetails(line1, benefitData.getBalanceState(), "");
            }

            incomeDetails.add(incomeDetail);
        }

        return incomeDetails;
    }
}
