package com.intelliviz.data;

import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;

import static com.intelliviz.lowlevel.util.RetirementConstants.BI_GOOD;

public class BaseSavingsIncomeDataAccessor extends AbstractIncomeDataAccessor {
    private AgeData mStartAge;
    private double mStartBalance;
    private double mBalance;
    private double mMonthlyDeposit;
    private double mInterest;
    private double mMonthlyInterest;
    private double mInitialWithdrawPercent;
    private double mMonthlyWithdraw;
    private String mPrimartBirthdate;

    public BaseSavingsIncomeDataAccessor(int owner, double balance, double monthlyDeposit, double interest, double initialWithdrawPercent) {
        super(owner);
        mBalance = balance;
        mMonthlyDeposit = monthlyDeposit;
        mMonthlyInterest = interest / 1200;
        mInitialWithdrawPercent = initialWithdrawPercent / 100;
    }

    @Override
    public IncomeData getIncomeData(AgeData age) {
        return null;
    }

    public IncomeData getIncomeData(IncomeData incomeData) {
        if(incomeData == null) {
            AgeData currentAge = AgeUtils.getAge(mPrimartBirthdate);
            return new IncomeData(currentAge, 0, mStartBalance, BI_GOOD, null);
        } else {
            AgeData age = incomeData.getAge();
            age = new AgeData(age.getNumberOfMonths()+1);
            double balance = incomeData.getBalance();
            balance += mMonthlyDeposit;
            double amount = balance * mMonthlyInterest;
            balance += amount;
            double monthlyAmount = balance * mInitialWithdrawPercent;
            return new IncomeData(age, monthlyAmount, balance, BI_GOOD, null);
        }
    }
}
