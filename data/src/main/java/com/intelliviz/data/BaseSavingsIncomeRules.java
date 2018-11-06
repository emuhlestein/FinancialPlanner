package com.intelliviz.data;

import android.os.Bundle;

import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;
import com.intelliviz.lowlevel.util.RetirementConstants;

import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_ANNUAL_PERCENT_INCREASE;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_MONTHLY_ADDITION;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SHOW_MONTHS;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SOURCE_BALANCE;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_SOURCE_INTEREST;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_START_AGE;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_STOP_AGE;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_WITHDRAW_PERCENT;
import static com.intelliviz.lowlevel.util.RetirementConstants.OWNER_PRIMARY;


/**
 * This class calculates the savings balance and potential monthly withdraw for a given age. These values
 * are based on several inputs: balance, monthly deposit, start age, annual interest rate, initial monthly
 * withdraw, annual percent increase and stop age.
 *
 * Monthly withdraws begin at the start age. At the start age, the initial monthly withdrawal is calculated.
 * This is based on the balance at that age and the initial withdrawal percentage. The initial withdrawal
 * percentage is the percentage of the balance that will be the initial withdrawal.
 *
 * Before the start age, the monthly withdrawal amount is 0.
 *
 * The start age can be overridden. The purpose of this is to allow the balance to grow and hence, the monthly
 * withdraw amount. The purpose of this is to allow the balance to grow without making withdrawals. This
 * to show how the balance and the monthly withdraw change over time.
 *
 * Created by edm on 12/30/2017.
 */

public abstract class BaseSavingsIncomeRules implements IncomeTypeRules {
    private int mOwner;
    private String mOwnerBirthdate;
    private String mOtherBirthdate;
    private AgeData mStartAge; // age at which withdraws begin
    private AgeData mEndAge; // end of life
    private AgeData mStopAge; // age at which monthly deposits stop
    private double mBalance; // starting balance
    private double mInterest; // annual interest (APR)
    private double mMonthlyDeposit; // amount that is deposited each month
    private double mInitialWithdrawPercent; // The percentage of balance for initial withdraw.
    private double mAnnualPercentIncrease; // percent to increase withdraw
    private boolean mShowMonths;
    private RetirementOptions mRO;

    private double mCurrentBalance;
    private AgeData mCurrentAge;
    private AgeData mCurrentStartAge;
    private boolean mMakeWithdraws; // if th is is true, mStartAge needs to apply.

    BaseSavingsIncomeRules(RetirementOptions ro, boolean makeWithdraws) {
        mRO = ro;
        mOwnerBirthdate = ro.getPrimaryBirthdate();
        mEndAge = ro.getEndAge();
        mOtherBirthdate = ro.getSpouseBirthdate();
        mMakeWithdraws = makeWithdraws;
    }

    public int getOwner() {
        return mOwner;
    }

    public RetirementOptions getRetirementOptions() { return mRO; }

    protected abstract IncomeData createIncomeData(AgeData age, double monthlyAmount, double balance);

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

        mCurrentBalance = mBalance;
        mCurrentAge = currentAge;
        mCurrentStartAge = mStartAge;
    }

    @Override
    public IncomeData getIncomeData(AgeData primaryAge) {
        double monthlyWithdraw = 0;
        double monthlyDeposit = mMonthlyDeposit;
        double initWithdrawPercent = mInitialWithdrawPercent / 100;
        double monthlyInterest = mInterest / 1200;

        AgeData age = convertAge(primaryAge);

        if (age.equals(mCurrentAge)) {
            if(age.isOnOrAfter(mStartAge)) {
                monthlyWithdraw = mCurrentBalance * initWithdrawPercent / 12;
            } else {
                monthlyWithdraw = 0;
            }
            return createIncomeData(primaryAge, monthlyWithdraw, mCurrentBalance);
        } else if (age.isBefore(mCurrentAge)) {
            return  createIncomeData(primaryAge, 0, 0);
        }

        int numMonths = age.diff(mCurrentAge);
        int month = mCurrentAge.getNumberOfMonths();
        int totalMonths = month + numMonths;

        for (; month < totalMonths; month++) {
            AgeData currentAge = new AgeData(month);
            if(mMakeWithdraws) {
                monthlyWithdraw = getMonthlyWithdraw(currentAge, mStartAge, monthlyWithdraw, mCurrentBalance, initWithdrawPercent);
                mCurrentBalance -= monthlyWithdraw;
                if (mCurrentBalance < 0) {
                    mCurrentBalance += monthlyWithdraw;
                    monthlyWithdraw = mCurrentBalance;
                    mCurrentBalance = 0;
                }
            } else {
                monthlyWithdraw = mCurrentBalance * initWithdrawPercent / 12;
            }

            if (currentAge.isOnOrAfter(mStopAge)) {
                monthlyDeposit = 0;
            }
            mCurrentBalance += monthlyDeposit;

            double amount = mCurrentBalance * monthlyInterest;
            mCurrentBalance += amount;
        }

        mCurrentAge = new AgeData(month);

        return createIncomeData(mCurrentAge, monthlyWithdraw, mCurrentBalance);
    }

    private double getMonthlyWithdraw(AgeData age, AgeData startAge, double monthlyWithdraw, double balance, double initWithdrawPercent) {
        if (age.isOnOrAfter(startAge)) {
            if (age.equals(startAge)) {
                monthlyWithdraw = balance * initWithdrawPercent / 12;
            } else {
                if (age.getMonth() == 0) {
                    monthlyWithdraw = monthlyWithdraw + (monthlyWithdraw * mAnnualPercentIncrease / 100);
                }
            }
        } else {
            monthlyWithdraw = 0;
        }

        return monthlyWithdraw;
    }

    private AgeData convertAge(AgeData age) {
        if(mOwner == OWNER_PRIMARY) {
            return age;
        } else {
            return AgeUtils.getAge(mRO.getPrimaryBirthdate(), mRO.getSpouseBirthdate(), age);
        }
    }
}
