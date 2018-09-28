package com.intelliviz.data;

import android.os.Bundle;

import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;
import com.intelliviz.lowlevel.util.RetirementConstants;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_ANNUAL_PERCENT_INCREASE;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_MONTHLY_ADDITION;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SHOW_MONTHS;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SOURCE_BALANCE;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SOURCE_INTEREST;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_START_AGE;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_STOP_AGE;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_WITHDRAW_PERCENT;


/**
 * Created by edm on 12/30/2017.
 */

public abstract class BaseSavingsIncomeRules {
    private int mOwner;
    private String mOwnerBirthdate;
    private String mOtherBirthdate;
    private AgeData mStartAge; // age at which withdraws begin
    private AgeData mEndAge; // end of life
    private AgeData mStopAge; // age at which monthly deposits stop
    private double mBalance; // balance
    private double mInterest; // annual interest (APR)
    private double mMonthlyDeposit; // amount that is deposited each month
    private double mInitialWithdrawPercent; // The percentage of balance for initial withdraw.
    private double mAnnualPercentIncrease; // percent to increase withdraw
    private boolean mShowMonths;
    private RetirementOptions mRO;

    BaseSavingsIncomeRules(RetirementOptions ro) {
        mRO = ro;
        mOwnerBirthdate = ro.getPrimaryBirthdate();
        mEndAge = ro.getEndAge();
        mOtherBirthdate = ro.getSpouseBirthdate();
    }

    public int getOwner() {
        return mOwner;
    }

    public RetirementOptions getRetirementOptions() { return mRO; }

    protected abstract IncomeData createIncomeData(AgeData age, double monthlyAmount, double balance);

    protected abstract IncomeDataAccessor getIncomeDataAccessor();

    public void setValues(Bundle bundle) {
        mOwner = bundle.getInt(RetirementConstants.EXTRA_INCOME_OWNER);
        mBalance = bundle.getDouble(EXTRA_INCOME_SOURCE_BALANCE);
        mInterest = bundle.getDouble(EXTRA_INCOME_SOURCE_INTEREST);
        mMonthlyDeposit = bundle.getDouble(EXTRA_INCOME_MONTHLY_ADDITION);
        mInitialWithdrawPercent = bundle.getDouble(EXTRA_INCOME_WITHDRAW_PERCENT);
        mAnnualPercentIncrease = bundle.getDouble(EXTRA_ANNUAL_PERCENT_INCREASE);
        mStartAge = bundle.getParcelable(EXTRA_INCOME_START_AGE);
        mStopAge = bundle.getParcelable(EXTRA_INCOME_STOP_AGE);
        mShowMonths = bundle.getInt(EXTRA_INCOME_SHOW_MONTHS) == 1;

        if(mOwner == RetirementConstants.OWNER_SPOUSE) {
            mOwnerBirthdate = mRO.getSpouseBirthdate();
            mOtherBirthdate = mRO.getPrimaryBirthdate();
        }

        AgeData currentAge = AgeUtils.getAge(mOwnerBirthdate);

        // no age can be before current age.
        if (mStartAge.isBefore(currentAge)) {
            mStartAge = new AgeData(currentAge.getNumberOfMonths());
        }

        if (mStopAge.isBefore(currentAge)) {
            mStopAge = new AgeData(currentAge.getNumberOfMonths());
        }
    }

    public List<IncomeData> getIncomeData() {
        double monthlyWithdraw = 0;
        double balance = mBalance;
        double monthlyDeposit = mMonthlyDeposit;
        double initWithdrawPercent = mInitialWithdrawPercent / 100;
        double monthlyInterest = mInterest / 1200;
        AgeData currentAge = AgeUtils.getAge(mOwnerBirthdate);

        List<IncomeData> listAmountDate = new ArrayList<>();

        for (int month = currentAge.getNumberOfMonths(); month <= mEndAge.getNumberOfMonths(); month++) {
            AgeData age = new AgeData(month);
            if (age.isOnOrAfter(mStartAge)) {
                if (age.equals(mStartAge)) {
                    monthlyWithdraw = balance * initWithdrawPercent / 12;
                } else {
                    if (age.getMonth() == 0) {
                        monthlyWithdraw = monthlyWithdraw + (monthlyWithdraw * mAnnualPercentIncrease / 100);
                    }
                }
            } else {
                monthlyWithdraw = 0;
            }

            balance -= monthlyWithdraw;
            if(balance < 0) {
                balance += monthlyWithdraw;
                monthlyWithdraw = balance;
                balance = 0;
            }

            listAmountDate.add(createIncomeData(age, monthlyWithdraw, balance));

            if (age.isAfter(mStopAge)) {
                monthlyDeposit = 0;
            }
            balance += monthlyDeposit;

            double amount = balance * monthlyInterest;
            balance += amount;
        }

        return listAmountDate;
    }
}
