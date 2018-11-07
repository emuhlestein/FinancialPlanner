package com.intelliviz.data;

import android.os.Bundle;

import com.intelliviz.db.entity.AbstractIncomeSource;
import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.RetirementConstants;

public class SavingsData extends AbstractIncomeSource {
    private AgeData mStartAge;
    private String mBalance;
    private String mInterest;
    private String mMonthlyAddition;
    private AgeData mStopMonthlyAdditionAge;
    private String mWithdrawPercent;
    private String mAnnualPercentIncrease;
    private int mShowMonths;
    private BaseSavingsIncomeRules mRules;

    public SavingsData(long id, int type) {
        super(id, type, "", RetirementConstants.OWNER_PRIMARY, 1);
    }

    public SavingsData(long id, int type, String name, int owner, int included) {
        super(id, type, name, owner, included);
    }

    public SavingsData(long id, int type, String name, int owner, int included,
                         AgeData startAge, String balance, String interest,
                         String monthlyAddition, AgeData stopMonthlyAdditionAge,
                         String withdrawPercent, String annualPercentIncrease, int showMonths) {
        super(id, type, name, owner, included);
        mStartAge = startAge;
        mBalance = balance;
        mInterest = interest;
        mMonthlyAddition = monthlyAddition;
        mStopMonthlyAdditionAge = stopMonthlyAdditionAge;
        mWithdrawPercent = withdrawPercent;
        mAnnualPercentIncrease = annualPercentIncrease;
        mShowMonths = showMonths;
    }

    public SavingsData(int owner,
                       AgeData startAge, String balance, String interest,
                       String monthlyAddition, AgeData stopMonthlyAdditionAge,
                       String withdrawPercent, String annualPercentIncrease) {
        super(-1, 0, "", owner, 1);
        mStartAge = startAge;
        mBalance = balance;
        mInterest = interest;
        mMonthlyAddition = monthlyAddition;
        mStopMonthlyAdditionAge = stopMonthlyAdditionAge;
        mWithdrawPercent = withdrawPercent;
        mAnnualPercentIncrease = annualPercentIncrease;
        mShowMonths = 0;
    }

    public void setStartAge(AgeData startAge) {
        mStartAge = startAge;
    }

    public void setBalance(String balance) {
        mBalance = balance;
    }

    public void setInterest(String interest) {
        mInterest = interest;
    }

    public void setMonthlyAddition(String monthlyAddition) {
        mMonthlyAddition = monthlyAddition;
    }

    public void setStopMonthlyAdditionAge(AgeData stopMonthlyAdditionAge) {
        mStopMonthlyAdditionAge = stopMonthlyAdditionAge;
    }

    public void setWithdrawPercent(String withdrawPercent) {
        mWithdrawPercent = withdrawPercent;
    }

    public void setAnnualPercentIncrease(String annualPercentIncrease) {
        mAnnualPercentIncrease = annualPercentIncrease;
    }

    public void setShowMonths(int showMonths) {
        mShowMonths = showMonths;
    }

    public AgeData getStartAge() {
        return mStartAge;
    }

    public String getBalance() {
        return mBalance;
    }

    public String getInterest() {
        return mInterest;
    }

    public String getMonthlyAddition() {
        return mMonthlyAddition;
    }

    public AgeData getStopMonthlyAdditionAge() {
        return mStopMonthlyAdditionAge;
    }

    public String getWithdrawPercent() {
        return mWithdrawPercent;
    }

    public String getAnnualPercentIncrease() {
        return mAnnualPercentIncrease;
    }

    public int getShowMonths() {
        return mShowMonths;
    }

    public void setRules(IncomeTypeRules rules) {
        if(rules instanceof SavingsIncomeRules) {
            mRules = (SavingsIncomeRules)rules;
        } else if (rules instanceof Savings401kIncomeRules) {
            mRules = (Savings401kIncomeRules)rules;
        }

        if(mRules != null) {
            Bundle bundle = new Bundle();
            bundle.putInt(RetirementConstants.EXTRA_INCOME_OWNER, getOwner());
            bundle.putDouble(RetirementConstants.EXTRA_INCOME_SOURCE_BALANCE, Double.parseDouble(mBalance));
            bundle.putDouble(RetirementConstants.EXTRA_INCOME_SOURCE_INTEREST, Double.parseDouble(mInterest));
            bundle.putDouble(RetirementConstants.EXTRA_INCOME_MONTHLY_ADDITION, Double.parseDouble(mMonthlyAddition));
            bundle.putDouble(RetirementConstants.EXTRA_INCOME_WITHDRAW_PERCENT, Double.parseDouble(mWithdrawPercent));
            bundle.putDouble(RetirementConstants.EXTRA_ANNUAL_PERCENT_INCREASE, Double.parseDouble(mAnnualPercentIncrease));
            bundle.putParcelable(RetirementConstants.EXTRA_INCOME_START_AGE, mStartAge);
            bundle.putParcelable(RetirementConstants.EXTRA_INCOME_STOP_AGE, mStopMonthlyAdditionAge);
            bundle.putInt(RetirementConstants.EXTRA_INCOME_SHOW_MONTHS, mShowMonths);
            mRules.setValues(bundle);
        }
    }

    @Override
    public IncomeData getIncomeData(AgeData age) {
        if(mRules != null) {
            return mRules.getIncomeData(age);
        } else {
            return null;
        }
    }
}
