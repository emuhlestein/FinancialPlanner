package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.data.GovPension;
import com.intelliviz.data.IncomeSourceType;
import com.intelliviz.data.PensionData;
import com.intelliviz.data.PensionRules;
import com.intelliviz.data.RetirementOptions;
import com.intelliviz.data.Savings401kIncomeRules;
import com.intelliviz.data.SavingsData;
import com.intelliviz.data.SavingsIncomeRules;
import com.intelliviz.data.SocialSecurityRules;
import com.intelliviz.db.AppDatabase;
import com.intelliviz.db.entity.GovPensionEntity;
import com.intelliviz.db.entity.GovPensionEntityMapper;
import com.intelliviz.db.entity.IncomeSourceEntityBase;
import com.intelliviz.db.entity.PensionDataEntityMapper;
import com.intelliviz.db.entity.PensionIncomeEntity;
import com.intelliviz.db.entity.RetirementOptionsEntity;
import com.intelliviz.db.entity.RetirementOptionsMapper;
import com.intelliviz.db.entity.SavingsDataEntityMapper;
import com.intelliviz.db.entity.SavingsIncomeEntity;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm on 10/7/2017.
 */

public class NavigationModelView extends AndroidViewModel {
    private AppDatabase mDB;
    private MutableLiveData<RetirementOptionsEntity> mROE = new MutableLiveData<>();
    private MutableLiveData<String> mMonthlyBenefit = new MutableLiveData<>();

    public NavigationModelView(Application application) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetRetirementOptionsAsyncTask().execute();
    }

    public LiveData<RetirementOptionsEntity> getROE() {
        return mROE;
    }

    public void update(RetirementOptionsEntity roe) {
        new UpdateRetirementOptionsAsyncTask().execute(roe);
        mROE.setValue(roe);
    }

    public void update() {
        new GetRetirementOptionsAsyncTask().execute();
    }

    public void updateBirthdate(String birthdate, int includeSpouse, String spouseBirthdate) {
        RetirementOptionsEntity roe = mROE.getValue();
        RetirementOptionsEntity newRom = new RetirementOptionsEntity(roe.getId(), roe.getEndAge(), roe.getSpouseEndAge(), birthdate, includeSpouse, spouseBirthdate, roe.getCountryCode());
        mROE.setValue(newRom);
        new UpdateRetirementOptionsAsyncTask().execute(newRom);
    }

    public LiveData<String> getMonthlyAmount() {
        return mMonthlyBenefit;
    }

    /**
     * Calculate when one can retire based on when will have a monthly benefit of monthlyBenefit.
     * @param monthlyBenefit The desired monthly benefit at retirement.
     */
    public void whenCanRetire(String monthlyBenefit) {
        new GetAllIncomeSummariesAsyncTask().execute();
    }

    private class GetRetirementOptionsAsyncTask extends android.os.AsyncTask<Void, Void, RetirementOptionsEntity> {

        @Override
        protected  RetirementOptionsEntity doInBackground(Void... params) {
            return mDB.retirementOptionsDao().get();
        }

        @Override
        protected void onPostExecute(RetirementOptionsEntity rom) {
            mROE.setValue(rom);
        }
    }

    private class UpdateRetirementOptionsAsyncTask extends android.os.AsyncTask<RetirementOptionsEntity, Void, RetirementOptionsEntity> {

        @Override
        protected RetirementOptionsEntity doInBackground(RetirementOptionsEntity... params) {
            RetirementOptionsEntity roe1 = mDB.retirementOptionsDao().get();
            RetirementOptionsEntity roe = params[0];
            roe1.setBirthdate(roe.getBirthdate());
            roe1.setSpouseBirthdate(roe.getSpouseBirthdate());
            roe1.setIncludeSpouse(roe.getIncludeSpouse());
            mDB.retirementOptionsDao().update(roe);
            // TODO when ROM is updated, everything should be updated.
            // SystemUtils.updateAppWidget(getApplication());
            return roe1;
        }

        @Override
        protected void onPostExecute(RetirementOptionsEntity roe) {
            mROE.setValue(roe);
        }
    }

    // TODO this is copied
    private class GetAllIncomeSummariesAsyncTask extends AsyncTask<String, Void, AgeData> {

        @Override
        protected AgeData doInBackground(String... params) {

            List<IncomeSourceEntityBase> listIncomeSources = getAllIncomeSources();
            RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
            RetirementOptions ro = RetirementOptionsMapper.map(roe);

            List<IncomeSourceType> incomeSources = new ArrayList<>();
            List<GovPension> gpList = new ArrayList<>();
            for(IncomeSourceEntityBase incomeSource : listIncomeSources) {
                if(incomeSource instanceof GovPensionEntity) {
                    GovPension gp = GovPensionEntityMapper.map((GovPensionEntity)incomeSource);
                    incomeSources.add(gp);
                    gpList.add(gp);
                } else if(incomeSource instanceof PensionIncomeEntity) {
                    PensionData pd = PensionDataEntityMapper.map((PensionIncomeEntity)incomeSource);
                    pd.setRules(new PensionRules(ro));
                    incomeSources.add(pd);
                } else if(incomeSource instanceof SavingsIncomeEntity) {
                    SavingsIncomeEntity sie = (SavingsIncomeEntity)incomeSources;
                    SavingsData sd = SavingsDataEntityMapper.map(sie);
                    if (sd.getType() == RetirementConstants.INCOME_TYPE_SAVINGS) {
                        SavingsIncomeRules sir = new SavingsIncomeRules(ro, true);
                        sd.setRules(sir);
                        incomeSources.add(sd);
                    } else if (sd.getType() == RetirementConstants.INCOME_TYPE_401K) {
                        Savings401kIncomeRules tdir = new Savings401kIncomeRules(ro, true);
                        sd.setRules(tdir);
                        incomeSources.add(sd);
                    }
                }
            }

            if(!gpList.isEmpty()) {
                SocialSecurityRules.setRulesOnGovPensionEntities(gpList, ro);
            }
            return new AgeData(70, 0);
        }

        @Override
        protected void onPostExecute(AgeData age) {
            mMonthlyBenefit.setValue(age.toString());
        }
    }

    // TODO need to make this global
    private List<IncomeSourceEntityBase> getAllIncomeSources() {
        List<IncomeSourceEntityBase> incomeSourceList = new ArrayList<>();
        List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
        if (gpeList != null) {
            for (GovPensionEntity gpe : gpeList) {
                incomeSourceList.add(gpe);
            }
        }

        List<PensionIncomeEntity> pieList = mDB.pensionIncomeDao().get();
        if (pieList != null) {
            for (PensionIncomeEntity pie : pieList) {
                incomeSourceList.add(pie);
            }
        }

        List<SavingsIncomeEntity> savingsList = mDB.savingsIncomeDao().get();
        if (savingsList != null) {
            for (SavingsIncomeEntity savings : savingsList) {
                incomeSourceList.add(savings);
            }
        }

        return incomeSourceList;
    }
}
