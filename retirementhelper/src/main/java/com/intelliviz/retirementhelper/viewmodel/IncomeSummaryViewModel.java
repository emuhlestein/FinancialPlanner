package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.BenefitData;
import com.intelliviz.retirementhelper.data.PensionRules;
import com.intelliviz.retirementhelper.data.Savings401kIncomeRules;
import com.intelliviz.retirementhelper.data.SavingsIncomeRules;
import com.intelliviz.retirementhelper.data.SocialSecurityRules;
import com.intelliviz.retirementhelper.db.AppDatabase;
import com.intelliviz.retirementhelper.db.entity.GovPensionEntity;
import com.intelliviz.retirementhelper.db.entity.PensionIncomeEntity;
import com.intelliviz.retirementhelper.db.entity.RetirementOptionsEntity;
import com.intelliviz.retirementhelper.db.entity.SavingsIncomeEntity;
import com.intelliviz.retirementhelper.util.RetirementConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by edm on 9/30/2017.
 */

public class IncomeSummaryViewModel extends AndroidViewModel {
    private MutableLiveData<List<BenefitData>> mAmountData = new MutableLiveData<>();
    private AppDatabase mDB;

    public IncomeSummaryViewModel(Application application) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetAmountDataAsyncTask().execute();
    }

    public LiveData<List<BenefitData>> getList() {
        return mAmountData;
    }

    public void update() {
        new GetAmountDataAsyncTask().execute();
    }

    private class GetAmountDataAsyncTask extends AsyncTask<Void, Void, List<BenefitData>> {

        @Override
        protected List<BenefitData> doInBackground(Void... voids) {
            return getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<BenefitData> benefitData) {
            mAmountData.setValue(benefitData);
        }
    }

    private List<BenefitData> getAllIncomeSources() {
        RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
        return getIncomeSummary(roe);
    }

    private List<BenefitData> getIncomeSummary(RetirementOptionsEntity roe) {
        List<BenefitData> benefitDataList = new ArrayList<>();

        List<SavingsIncomeEntity> tdieList = mDB.savingsIncomeDao().get();
        List<List<BenefitData>> incomeSourceEntityList = new ArrayList<>();
        for (SavingsIncomeEntity sie : tdieList) {
            if (sie.getType() == RetirementConstants.INCOME_TYPE_SAVINGS) {
                SavingsIncomeRules sir = new SavingsIncomeRules(roe.getBirthdate(), roe.getEndAge());
                sie.setRules(sir);
                incomeSourceEntityList.add(sie.getBenefitData());
            } else if (sie.getType() == RetirementConstants.INCOME_TYPE_401K) {
                Savings401kIncomeRules tdir = new Savings401kIncomeRules(roe.getBirthdate(), roe.getEndAge());
                sie.setRules(tdir);
                incomeSourceEntityList.add(sie.getBenefitData());
            }
        }

        List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
        SocialSecurityRules.setRulesOnGovPensionEntities(gpeList, roe);
        for (GovPensionEntity gpe : gpeList) {
            incomeSourceEntityList.add(gpe.getBenefitData());
        }

        List<PensionIncomeEntity> pieList = mDB.pensionIncomeDao().get();
        for (PensionIncomeEntity pie : pieList) {
            AgeData minAge = pie.getMinAge();
            PensionRules pr = new PensionRules(roe.getBirthdate(), minAge, roe.getEndAge(), Double.parseDouble(pie.getMonthlyBenefit()));
            pie.setRules(pr);
            incomeSourceEntityList.add(pie.getBenefitData());
        }

        if(incomeSourceEntityList.isEmpty()) {
            return Collections.emptyList();
        }

        int numYears = incomeSourceEntityList.get(0).size();

        BenefitData benefitData;
        for(int year = 0; year < numYears; year++) {
            double sumBalance = 0;
            double sumMonthlyWithdraw = 0;
            AgeData age = incomeSourceEntityList.get(0).get(0).getAge();

            for (List<BenefitData> bdList : incomeSourceEntityList) {
                benefitData = bdList.get(year);
                age = new AgeData(benefitData.getAge().getNumberOfMonths());
                sumBalance += benefitData.getBalance();
                sumMonthlyWithdraw += benefitData.getMonthlyAmount();
            }

            benefitDataList.add(new BenefitData(new AgeData(age.getNumberOfMonths()), sumMonthlyWithdraw, sumBalance, 0, false));
        }

        return benefitDataList;
    }
}
