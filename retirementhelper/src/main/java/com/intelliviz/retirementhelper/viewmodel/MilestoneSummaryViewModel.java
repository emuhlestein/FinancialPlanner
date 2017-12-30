package com.intelliviz.retirementhelper.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.AmountData;
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
import java.util.List;

/**
 * Created by edm on 9/30/2017.
 */

public class MilestoneSummaryViewModel extends AndroidViewModel {
    private MutableLiveData<List<AmountData>> mAmountData = new MutableLiveData<>();
    private AppDatabase mDB;

    public MilestoneSummaryViewModel(Application application) {
        super(application);
        mDB = AppDatabase.getInstance(application);
        new GetAmountDataAsyncTask().execute();
    }

    public LiveData<List<AmountData>> getList() {
        return mAmountData;
    }

    public void update() {
        new GetAmountDataAsyncTask().execute();
    }

    private class GetAmountDataAsyncTask extends AsyncTask<Void, Void, List<AmountData>> {

        @Override
        protected List<AmountData> doInBackground(Void... voids) {
            return  getAllIncomeSources();
        }

        @Override
        protected void onPostExecute(List<AmountData> amountData) {
            mAmountData.setValue(amountData);
        }
    }

    private List<AmountData> getAllIncomeSources() {
        List<List<AmountData>> allIncomeSources = new ArrayList<>();
        List<SavingsIncomeEntity> tdieList = mDB.savingsIncomeDao().get();
        RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
        AgeData endAge = roe.getEndAge();
        switch(roe.getCurrentOption()) {
            case RetirementConstants.INCOME_SUMMARY_MODE:
                return getIncomeSummary(roe);
            case RetirementConstants.REACH_AMOUNT_MODE:
                return getReachAmount(roe);
            case RetirementConstants.REACH_IMCOME_PERCENT_MODE:
                return getIncomeSummary(roe);
            default:

        }
        return getIncomeSummary(roe);
    }

    private List<AmountData> sumAmounts(AgeData endAge,  List<List<AmountData>> allIncomeSources) {
        List<AmountData> allAmounts = new ArrayList<>();
        int numIncomeSources = allIncomeSources.size();
        List<IndexAmount> indeces = new ArrayList<>();

        for(int incomeSource = 0; incomeSource < numIncomeSources; incomeSource++) {
            IndexAmount indexAmount = new IndexAmount();
            indexAmount.amountData = allIncomeSources.get(incomeSource);
            indexAmount.currentIndex = 0;
            indeces.add(indexAmount);
        }

        int lastMonth = endAge.getNumberOfMonths();
        for(int currentMonth = 0; currentMonth < lastMonth; currentMonth++) {
            double sumMonthlyAmount = 0;
            for (int incomeSource = 0; incomeSource < numIncomeSources; incomeSource++) {
                double monthlyAmount;
                List<AmountData> amountData = indeces.get(incomeSource).amountData;
                int index = indeces.get(incomeSource).currentIndex;
                if(amountData.get(index).getAge().getNumberOfMonths() == currentMonth) {
                    monthlyAmount = amountData.get(index).getMonthlyAmount();
                    sumMonthlyAmount += monthlyAmount;
                    indeces.get(incomeSource).currentIndex++;
                }
            }

            if(sumMonthlyAmount > 0) {
                //AgeData age, double monthlyAmount, double balance, int balanceState, boolean penalty)
                AgeData age = new AgeData(currentMonth);
                AmountData sumAmount = new AmountData(age, sumMonthlyAmount, 0, 0, false);
                allAmounts.add(sumAmount);
            }
        }

        return allAmounts;
    }

    private List<AmountData> getIncomeSummary(RetirementOptionsEntity roe) {
        float desiredBalance = Float.parseFloat(roe.getReachAmount());
        List<List<AmountData>> allIncomeSources = new ArrayList<>();
        List<SavingsIncomeEntity> tdieList = mDB.savingsIncomeDao().get();
        AgeData endAge = roe.getEndAge();
        for(SavingsIncomeEntity sie : tdieList) {
            AgeData startAge = sie.getStartAge();
            if(sie.getType() == RetirementConstants.INCOME_TYPE_SAVINGS) {
                SavingsIncomeRules sir = new SavingsIncomeRules(roe.getBirthdate(), endAge, startAge,
                        Double.parseDouble(sie.getBalance()),
                        Double.parseDouble(sie.getInterest()),
                        Double.parseDouble(sie.getMonthlyAddition()),
                        sie.getWithdrawMode(), Double.parseDouble(sie.getWithdrawAmount()));
                sie.setRules(sir);
                sie.getBalance();
                allIncomeSources.add(sie.getMonthlyAmountData());

            } else if(sie.getType() == RetirementConstants.INCOME_TYPE_401K) {

                Savings401kIncomeRules tdir = new Savings401kIncomeRules(roe.getBirthdate(), endAge, startAge, Double.parseDouble(sie.getBalance()),
                        Double.parseDouble(sie.getInterest()), Double.parseDouble(sie.getMonthlyAddition()), sie.getWithdrawMode(),
                        Double.parseDouble(sie.getWithdrawAmount()));
                sie.setRules(tdir);
                allIncomeSources.add(sie.getMonthlyAmountData());
            }

        }
        List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
        for(GovPensionEntity gpie : gpeList) {

            String birthdate = roe.getBirthdate();
            SocialSecurityRules ssr = new SocialSecurityRules(birthdate, endAge);
            gpie.setRules(ssr);
            allIncomeSources.add(gpie.getMonthlyAmountData());

        }
        List<PensionIncomeEntity> pieList = mDB.pensionIncomeDao().get();
        for(PensionIncomeEntity pie : pieList) {
            AgeData minAge = pie.getMinAge();
            PensionRules pr = new PensionRules(minAge, endAge,  Double.parseDouble(pie.getMonthlyBenefit()));
            pie.setRules(pr);
            allIncomeSources.add(pie.getMonthlyAmountData());
        }

        return sumAmounts(endAge, allIncomeSources);
    }

    private List<AmountData> getReachAmount(RetirementOptionsEntity roe) {
        List<List<AmountData>> allIncomeSources = new ArrayList<>();
        List<SavingsIncomeEntity> tdieList = mDB.savingsIncomeDao().get();
        AgeData endAge = roe.getEndAge();

        int currentMonth = 0;
        while(true) {
        for(SavingsIncomeEntity sie : tdieList) {
            AgeData startAge = sie.getStartAge();
            if (sie.getType() == RetirementConstants.INCOME_TYPE_SAVINGS) {
                SavingsIncomeRules sir = new SavingsIncomeRules(roe.getBirthdate(), endAge, startAge,
                        Double.parseDouble(sie.getBalance()),
                        Double.parseDouble(sie.getInterest()),
                        Double.parseDouble(sie.getMonthlyAddition()),
                        sie.getWithdrawMode(), Double.parseDouble(sie.getWithdrawAmount()));
                sie.setRules(sir);
                allIncomeSources.add(sie.getMonthlyAmountData());

            } else if (sie.getType() == RetirementConstants.INCOME_TYPE_401K) {

                Savings401kIncomeRules tdir = new Savings401kIncomeRules(roe.getBirthdate(), endAge, startAge, Double.parseDouble(sie.getBalance()),
                        Double.parseDouble(sie.getInterest()), Double.parseDouble(sie.getMonthlyAddition()), sie.getWithdrawMode(),
                        Double.parseDouble(sie.getWithdrawAmount()));
                sie.setRules(tdir);
                allIncomeSources.add(sie.getMonthlyAmountData());
            }
        }
        }
    }

    private static class IndexAmount {
        public List<AmountData> amountData;
        public int currentIndex;
    }
}
