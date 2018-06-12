package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.income.data.IncomeData;
import com.intelliviz.income.db.AppDatabase;
import com.intelliviz.income.db.entity.RetirementOptionsEntity;

import java.util.List;

/**
 * Created by edm on 9/30/2017.
 */

public class IncomeSummaryViewModel extends AndroidViewModel {
    private MutableLiveData<List<IncomeData>> mAmountData = new MutableLiveData<>();
    private AppDatabase mDB;

    public IncomeSummaryViewModel(Application application) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetAmountDataAsyncTask().execute();
    }

    public LiveData<List<IncomeData>> getList() {
        return mAmountData;
    }

    public void update() {
        new GetAmountDataAsyncTask().execute();
    }

    private class GetAmountDataAsyncTask extends AsyncTask<Void, Void, List<IncomeData>> {

        @Override
        protected List<IncomeData> doInBackground(Void... voids) {
            return getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<IncomeData> benefitData) {
            mAmountData.setValue(benefitData);
        }
    }

    private List<IncomeData> getAllIncomeSources() {
        RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
        return getIncomeSummary(roe);
    }

    private List<IncomeData> getIncomeSummary(RetirementOptionsEntity roe) {
        /*
        List<IncomeData> benefitDataList = new ArrayList<>();

        List<SavingsIncomeEntity> tdieList = mDB.savingsIncomeDao().get();
        List<List<IncomeData>> incomeSourceEntityList = new ArrayList<>();
        for (SavingsIncomeEntity sie : tdieList) {
            if (sie.getType() == RetirementConstants.INCOME_TYPE_SAVINGS) {
                SavingsIncomeRules sir = new SavingsIncomeRules(roe.getBirthdate(), roe.getEndAge());
                sie.setRules(sir);
                incomeSourceEntityList.add(sie.getIncomeData());
            } else if (sie.getType() == RetirementConstants.INCOME_TYPE_401K) {
                Savings401kIncomeRules tdir = new Savings401kIncomeRules(roe.getBirthdate(), roe.getEndAge());
                sie.setRules(tdir);
                incomeSourceEntityList.add(sie.getIncomeData());
            }
        }

        List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
        SocialSecurityRules.setRulesOnGovPensionEntities(gpeList, roe);
        for (GovPensionEntity gpe : gpeList) {
            incomeSourceEntityList.add(gpe.getIncomeData());
        }

        List<PensionIncomeEntity> pieList = mDB.pensionIncomeDao().get();
        for (PensionIncomeEntity pie : pieList) {
            AgeData minAge = pie.getMinAge();
            PensionRules pr = new PensionRules(roe.getBirthdate(), minAge, roe.getEndAge(), Double.parseDouble(pie.getMonthlyBenefit()));
            pie.setRules(pr);
            incomeSourceEntityList.add(pie.getIncomeData());
        }

        if(incomeSourceEntityList.isEmpty()) {
            return Collections.emptyList();
        }

        int numYears = incomeSourceEntityList.get(0).size();

        IncomeData benefitData;
        for(int year = 0; year < numYears; year++) {
            double sumBalance = 0;
            double sumMonthlyWithdraw = 0;
            AgeData age = incomeSourceEntityList.get(0).get(0).getAge();

            for (List<IncomeData> bdList : incomeSourceEntityList) {
                benefitData = bdList.get(year);
                age = new AgeData(benefitData.getAge().getNumberOfMonths());
                sumBalance += benefitData.getBalance();
                sumMonthlyWithdraw += benefitData.getMonthlyAmount();
            }

            benefitDataList.add(new IncomeData(new AgeData(age.getNumberOfMonths()), sumMonthlyWithdraw, sumBalance, 0, false));
        }

        return benefitDataList;
        */
        return IncomeSummaryHelper.getIncomeSummary(mDB, roe);
    }

}
