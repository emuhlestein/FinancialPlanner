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
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
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
        List<List<BenefitData>> allIncomeSources = new ArrayList<>();
        List<SavingsIncomeEntity> tdieList = mDB.savingsIncomeDao().get();
        RetirementOptionsEntity roe = mDB.retirementOptionsDao().get();
        AgeData endAge = roe.getEndAge();
        switch (roe.getCurrentOption()) {
            case RetirementConstants.INCOME_SUMMARY_MODE:
                return getIncomeSummary(roe);
            case RetirementConstants.REACH_AMOUNT_MODE:
                return getReachAmount(roe);
            case RetirementConstants.REACH_IMCOME_PERCENT_MODE:
                return getReachPercentIncome(roe);
            default:

        }
        return getIncomeSummary(roe);
    }

    private List<BenefitData> sumAmounts(AgeData currentAge, AgeData endAge, List<List<BenefitData>> allIncomeSources) {
        List<BenefitData> allAmounts = new ArrayList<>();
        int numIncomeSources = allIncomeSources.size();
        List<IndexAmount> indeces = new ArrayList<>();

        for (int incomeSource = 0; incomeSource < numIncomeSources; incomeSource++) {
            IndexAmount indexAmount = new IndexAmount();
            indexAmount.mBenefitData = allIncomeSources.get(incomeSource);
            indexAmount.currentIndex = 0;
            indeces.add(indexAmount);
        }

        int lastYear = endAge.getYear();
        AgeData age = currentAge;
        if (age.getMonth() > 0) {
            age = new AgeData(age.getYear() + 1, 0);
        }

        for (int year = age.getYear(); year < lastYear; year++) {
            double sumMonthlyAmount = 0;
            double sumBalance = 0;
            for (int incomeSource = 0; incomeSource < numIncomeSources; incomeSource++) {
                double monthlyAmount;
                double balance;
                List<BenefitData> benefitData = indeces.get(incomeSource).mBenefitData;
                int index = indeces.get(incomeSource).currentIndex;
                if (benefitData.get(index).getAge().getYear() == year) {
                    monthlyAmount = benefitData.get(index).getMonthlyAmount();
                    sumMonthlyAmount += monthlyAmount;

                    balance = benefitData.get(index).getBalance();
                    sumBalance += balance;
                    indeces.get(incomeSource).currentIndex++;
                }
            }

            age = new AgeData(year, 0);
            BenefitData sumAmount = new BenefitData(age, sumMonthlyAmount, sumBalance, RetirementConstants.BALANCE_STATE_GOOD, false);
            allAmounts.add(sumAmount);

        }

        return allAmounts;
    }

    private List<BenefitData> getIncomeSummaryOld(RetirementOptionsEntity roe) {
        List<List<BenefitData>> allIncomeSources = getBenefitDataList(roe);
        AgeData currentAge = SystemUtils.getAge(roe.getBirthdate());
        AgeData endAge = roe.getEndAge();
        return sumAmounts(currentAge, endAge, allIncomeSources);
    }

    private List<List<BenefitData>> getBenefitDataList(RetirementOptionsEntity roe) {
        List<List<BenefitData>> allIncomeSources = new ArrayList<>();
        List<SavingsIncomeEntity> tdieList = mDB.savingsIncomeDao().get();
        AgeData endAge = roe.getEndAge();

        for (SavingsIncomeEntity sie : tdieList) {
            if (sie.getType() == RetirementConstants.INCOME_TYPE_SAVINGS) {
                SavingsIncomeRules sir = new SavingsIncomeRules(roe.getBirthdate(), sie.getStartAge(), endAge,
                        Double.parseDouble(sie.getBalance()),
                        Double.parseDouble(sie.getInterest()),
                        Double.parseDouble(sie.getMonthlyAddition()),
                        sie.getWithdrawMode(), Double.parseDouble(sie.getWithdrawAmount()));
                sie.setRules(sir);
                allIncomeSources.add(sie.getBenefitData());

            } else if (sie.getType() == RetirementConstants.INCOME_TYPE_401K) {

                Savings401kIncomeRules tdir = new Savings401kIncomeRules(roe.getBirthdate(), sie.getStartAge(), endAge, Double.parseDouble(sie.getBalance()),
                        Double.parseDouble(sie.getInterest()), Double.parseDouble(sie.getMonthlyAddition()), sie.getWithdrawMode(),
                        Double.parseDouble(sie.getWithdrawAmount()));
                sie.setRules(tdir);
                allIncomeSources.add(sie.getBenefitData());
            }
        }
        List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
        SocialSecurityRules.setRulesOnGovPensionEntities(gpeList, roe);
        for (GovPensionEntity gpie : gpeList) {
            allIncomeSources.add(gpie.getBenefitData());
        }
        List<PensionIncomeEntity> pieList = mDB.pensionIncomeDao().get();
        for (PensionIncomeEntity pie : pieList) {
            AgeData minAge = pie.getMinAge();
            PensionRules pr = new PensionRules(roe.getBirthdate(), minAge, endAge, Double.parseDouble(pie.getMonthlyBenefit()));
            pie.setRules(pr);
            allIncomeSources.add(pie.getBenefitData());
        }

        return allIncomeSources;
    }

    private List<BenefitData> getIncomeSummary(RetirementOptionsEntity roe) {
        List<BenefitData> benefitDataList = new ArrayList<>();
        List<SavingsIncomeEntity> tdieList = mDB.savingsIncomeDao().get();
        BenefitData savingsBenefitData;
        BenefitData savings401kBenefitData;
        BenefitData savingsPensionBenefitData;
        BenefitData savingsGovPensionBenefitData;
        AgeData endAge = roe.getEndAge();

        AgeData currentAge = SystemUtils.getAge(roe.getBirthdate());
        for (int year = currentAge.getYear()+1; year < endAge.getYear(); year++) {
            AgeData age = new AgeData(year, 0);

            double sumBalance = 0;
            double sumMonthlyBenefit = 0;

            for (SavingsIncomeEntity sie : tdieList) {
                if (sie.getType() == RetirementConstants.INCOME_TYPE_SAVINGS) {
                    SavingsIncomeRules sir = new SavingsIncomeRules(roe.getBirthdate(),  sie.getStartAge(), endAge,
                            Double.parseDouble(sie.getBalance()),
                            Double.parseDouble(sie.getInterest()),
                            Double.parseDouble(sie.getMonthlyAddition()),
                            sie.getWithdrawMode(), Double.parseDouble(sie.getWithdrawAmount()));
                    sie.setRules(sir);
                    savingsBenefitData = sie.getBenefitForAge(age);
                    if (savingsBenefitData != null) {
                        sumBalance += savingsBenefitData.getBalance();
                        sumMonthlyBenefit += savingsBenefitData.getMonthlyAmount();
                    }
                } else if (sie.getType() == RetirementConstants.INCOME_TYPE_401K) {

                    Savings401kIncomeRules tdir = new Savings401kIncomeRules(roe.getBirthdate(), sie.getStartAge(), endAge, Double.parseDouble(sie.getBalance()),
                            Double.parseDouble(sie.getInterest()), Double.parseDouble(sie.getMonthlyAddition()), sie.getWithdrawMode(),
                            Double.parseDouble(sie.getWithdrawAmount()));
                    sie.setRules(tdir);
                    savings401kBenefitData = sie.getBenefitForAge(age);

                    if (savings401kBenefitData != null) {
                        sumBalance += savings401kBenefitData.getBalance();
                        sumMonthlyBenefit += savings401kBenefitData.getMonthlyAmount();
                    }
                }
            }

            List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
            SocialSecurityRules.setRulesOnGovPensionEntities(gpeList, roe);
            for (GovPensionEntity gpie : gpeList) {
                BenefitData benefitData = gpie.getBenefitForAge(age);
                sumMonthlyBenefit += benefitData.getMonthlyAmount();
            }
            List<PensionIncomeEntity> pieList = mDB.pensionIncomeDao().get();
            for (PensionIncomeEntity pie : pieList) {
                AgeData minAge = pie.getMinAge();
                PensionRules pr = new PensionRules(roe.getBirthdate(), minAge, endAge, Double.parseDouble(pie.getMonthlyBenefit()));
                pie.setRules(pr);
                savingsPensionBenefitData = pie.getBenefitForAge(age);
                if (savingsPensionBenefitData != null) {
                    sumBalance += savingsPensionBenefitData.getBalance();
                    sumMonthlyBenefit += savingsPensionBenefitData.getMonthlyAmount();
                }
            }

            BenefitData benefitData = new BenefitData(age, sumMonthlyBenefit, sumBalance, RetirementConstants.BALANCE_STATE_GOOD, false);
            benefitDataList.add(benefitData);
        }

        return benefitDataList;
    }

    private List<BenefitData> getReachPercentIncome(RetirementOptionsEntity roe) {
        List<BenefitData> benefitDataList = new ArrayList<>();
        List<SavingsIncomeEntity> tdieList = mDB.savingsIncomeDao().get();
        BenefitData savingsBenefitData = null;
        BenefitData savings401kBenefitData = null;
        BenefitData savingsPensionBenefitData = null;
        BenefitData savingsGovPensionBenefitData = null;
        AgeData age = SystemUtils.getAge(roe.getBirthdate());
        AgeData endAge = roe.getEndAge();
        double sumBalance = 0;

        String percentString = SystemUtils.getFloatValue(roe.getReachPercent());
        double percent = Double.parseDouble(percentString) / 100;
        String monthlySalaryString = SystemUtils.getFloatValue(roe.getMonthlyIncome());
        double monthlySalary = Double.parseDouble(monthlySalaryString);
        double targetAmount = monthlySalary * percent;

        while(true) {
            double balance = 0;
            for (SavingsIncomeEntity sie : tdieList) {
                if (sie.getType() == RetirementConstants.INCOME_TYPE_SAVINGS) {
                    SavingsIncomeRules sir = new SavingsIncomeRules(roe.getBirthdate(),  sie.getStartAge(), endAge,
                            Double.parseDouble(sie.getBalance()),
                            Double.parseDouble(sie.getInterest()),
                            Double.parseDouble(sie.getMonthlyAddition()),
                            sie.getWithdrawMode(), Double.parseDouble(sie.getWithdrawAmount()));
                    sie.setRules(sir);
                    savingsBenefitData = sie.getBenefitForAge(age);
                    if(savingsBenefitData != null) {
                        balance += savingsBenefitData.getBalance();
                    }

                } else if (sie.getType() == RetirementConstants.INCOME_TYPE_401K) {

                    Savings401kIncomeRules tdir = new Savings401kIncomeRules(roe.getBirthdate(), sie.getStartAge(), endAge, Double.parseDouble(sie.getBalance()),
                            Double.parseDouble(sie.getInterest()), Double.parseDouble(sie.getMonthlyAddition()), sie.getWithdrawMode(),
                            Double.parseDouble(sie.getWithdrawAmount()));
                    sie.setRules(tdir);
                    savings401kBenefitData = sie.getBenefitForAge(age);

                    if(savings401kBenefitData != null) {
                        balance += savings401kBenefitData.getBalance();
                    }
                }
            }

            double montlySavingsBenefit = 0;
            if(balance > sumBalance) {
                sumBalance = balance;
                // TODO make these finals: .04 is initial annual withdraw
                montlySavingsBenefit = sumBalance * .04 / 12;
            }

            double sumMonthlyBenefit = montlySavingsBenefit;

            List<GovPensionEntity> gpeList = mDB.govPensionDao().get();
            SocialSecurityRules.setRulesOnGovPensionEntities(gpeList, roe);
            for(GovPensionEntity gpe : gpeList) {
                BenefitData benefitData = gpe.getBenefitForAge(age);
                sumMonthlyBenefit += benefitData.getMonthlyAmount();
            }

            List<PensionIncomeEntity> pieList = mDB.pensionIncomeDao().get();
            for(PensionIncomeEntity pie : pieList) {
                AgeData minAge = pie.getMinAge();
                PensionRules pr = new PensionRules(roe.getBirthdate(), minAge, endAge,  Double.parseDouble(pie.getMonthlyBenefit()));
                pie.setRules(pr);
                BenefitData benefitData = pie.getBenefitForAge(age);
                sumMonthlyBenefit += benefitData.getMonthlyAmount();
            }

            BenefitData benefitData = new BenefitData(age, sumMonthlyBenefit, sumBalance, RetirementConstants.BALANCE_STATE_GOOD, false);
            benefitDataList.add(benefitData);

            if(sumMonthlyBenefit > targetAmount) {
                return benefitDataList;
            }

            age = new AgeData(age.getYear()+1, 0);
            if(age.getYear() >= 100) {
                return benefitDataList;
            }
        }
    }

    private List<BenefitData> getReachAmount(RetirementOptionsEntity roe) {
        List<List<BenefitData>> allIncomeSources = new ArrayList<>();
        List<SavingsIncomeEntity> tdieList = mDB.savingsIncomeDao().get();
        AgeData endAge = roe.getEndAge();

        int currentMonth = 0;
        while(true) {
        for(SavingsIncomeEntity sie : tdieList) {
            AgeData startAge = sie.getStartAge();
            if (sie.getType() == RetirementConstants.INCOME_TYPE_SAVINGS) {
                SavingsIncomeRules sir = new SavingsIncomeRules(roe.getBirthdate(), startAge, endAge,
                        Double.parseDouble(sie.getBalance()),
                        Double.parseDouble(sie.getInterest()),
                        Double.parseDouble(sie.getMonthlyAddition()),
                        sie.getWithdrawMode(), Double.parseDouble(sie.getWithdrawAmount()));
                sie.setRules(sir);
                allIncomeSources.add(sie.getBenefitData());

            } else if (sie.getType() == RetirementConstants.INCOME_TYPE_401K) {

                Savings401kIncomeRules tdir = new Savings401kIncomeRules(roe.getBirthdate(), startAge, endAge, Double.parseDouble(sie.getBalance()),
                        Double.parseDouble(sie.getInterest()), Double.parseDouble(sie.getMonthlyAddition()), sie.getWithdrawMode(),
                        Double.parseDouble(sie.getWithdrawAmount()));
                sie.setRules(tdir);
                allIncomeSources.add(sie.getBenefitData());
            }
        }
        }
    }

    private static class IndexAmount {
        public List<BenefitData> mBenefitData;
        public int currentIndex;
    }
}
