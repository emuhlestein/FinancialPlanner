package com.intelliviz.data;

import android.os.Bundle;

import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;
import com.intelliviz.lowlevel.util.RetirementConstants;

import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_ANNUAL_PERCENT_INCREASE;
import static com.intelliviz.lowlevel.util.RetirementConstants.EXTRA_INCOME_MONTHLY_ADDITION;
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
    private AgeData mStartWithdrawalAge; // age at which withdrawals begin
    private AgeData mStopDepositsAge; // age at which monthly deposits stop
    private double mBalance; // starting balance
    private double mInterest; // annual interest (APR)
    private double mMonthlyDeposit; // amount that is deposited each month
    private double mInitialWithdrawPercent; // The percentage of balance for initial withdraw.
    private double mAnnualPercentIncrease; // percent to increase withdraw
    private RetirementOptions mRO;

    private double mCurrentBalance;
    private AgeData mCurrentAge;
    private AgeData mCurrentStartAge;
    private boolean mMakeWithdraws; // if th is is true, mStartWithdrawalAge needs to apply.
    private boolean mOverride;

    BaseSavingsIncomeRules(RetirementOptions ro, boolean override) {
        mRO = ro;
        mOwnerBirthdate = ro.getPrimaryBirthdate();
        mOtherBirthdate = ro.getSpouseBirthdate();
        mOverride = override;
    }

    public RetirementOptions getRetirementOptions() { return mRO; }

    protected abstract IncomeData createIncomeData(AgeData age, double monthlyAmount, double balance);

    @Override
    public int getOwner() {
        return mOwner;
    }

    @Override
    public void setValues(Bundle bundle) {
        mOwner = bundle.getInt(RetirementConstants.EXTRA_INCOME_OWNER);
        mBalance = bundle.getDouble(EXTRA_INCOME_SOURCE_BALANCE);
        mInterest = bundle.getDouble(EXTRA_INCOME_SOURCE_INTEREST);
        mMonthlyDeposit = bundle.getDouble(EXTRA_INCOME_MONTHLY_ADDITION);
        mInitialWithdrawPercent = bundle.getDouble(EXTRA_INCOME_WITHDRAW_PERCENT);
        mAnnualPercentIncrease = bundle.getDouble(EXTRA_ANNUAL_PERCENT_INCREASE);
        mStartWithdrawalAge = bundle.getParcelable(EXTRA_INCOME_START_AGE);
        mStopDepositsAge = bundle.getParcelable(EXTRA_INCOME_STOP_AGE);

        if(mOwner == RetirementConstants.OWNER_SPOUSE) {
            mOwnerBirthdate = mRO.getSpouseBirthdate();
            mOtherBirthdate = mRO.getPrimaryBirthdate();
        }

        AgeData currentAge = AgeUtils.getAge(mOwnerBirthdate);

        // no age can be before current age.
        if (mStartWithdrawalAge.isBefore(currentAge)) {
            mStartWithdrawalAge = new AgeData(currentAge.getNumberOfMonths());
        }

        if (mStopDepositsAge.isBefore(currentAge)) {
            mStopDepositsAge = new AgeData(currentAge.getNumberOfMonths());
        }

        if(mOverride) {
            mMakeWithdraws = false;
        } else {
            mMakeWithdraws = true;
        }

        mCurrentBalance = mBalance;
        mCurrentAge = currentAge;
        mCurrentStartAge = mStartWithdrawalAge;
    }

    @Override
    public IncomeData getIncomeData(AgeData primaryAge) {
        double monthlyWithdraw = 0;
        double monthlyDeposit = mMonthlyDeposit;
        double initWithdrawPercent = mInitialWithdrawPercent / 100;
        double monthlyInterest = mInterest / 1200;

        AgeData age = convertAge(primaryAge);

       /* if(mMakeWithdraws) {
            if (age.isBefore(mStartWithdrawalAge)) {
                return createIncomeData(primaryAge, 0, mCurrentBalance);
            } else {
                monthlyWithdraw = mCurrentBalance * initWithdrawPercent / 12;
            }
        } else {
            monthlyWithdraw = mCurrentBalance * initWithdrawPercent / 12;
        }
*/
        monthlyWithdraw = mCurrentBalance * initWithdrawPercent / 12;

       /* if (age.equals(mCurrentAge)) {
            if(age.isOnOrAfter(mStartWithdrawalAge)) {
                monthlyWithdraw = mCurrentBalance * initWithdrawPercent / 12;
            } else {
                monthlyWithdraw = 0;
            }
            return createIncomeData(primaryAge, monthlyWithdraw, mCurrentBalance);
        } else if (age.isBefore(mCurrentAge)) {
            return createIncomeData(primaryAge, 0, 0);
        }*/

        int numMonths = age.diff(mCurrentAge);
        int month = mCurrentAge.getNumberOfMonths();
        int totalMonths = month + numMonths;

        for (; month < totalMonths; month++) {
            AgeData currentAge = new AgeData(month);
           /* if(mMakeWithdraws) {
                monthlyWithdraw = getMonthlyWithdraw(currentAge, mStartWithdrawalAge, monthlyWithdraw, mCurrentBalance, initWithdrawPercent);
                mCurrentBalance -= monthlyWithdraw;
                if (mCurrentBalance < 0) {
                    mCurrentBalance += monthlyWithdraw;
                    monthlyWithdraw = mCurrentBalance;
                    mCurrentBalance = 0;
                }
            } else {
                monthlyWithdraw = mCurrentBalance * initWithdrawPercent / 12;
            }*/

            if (currentAge.isOnOrAfter(mStopDepositsAge)) {
                monthlyDeposit = 0;
            }
            mCurrentBalance += monthlyDeposit;

            double amount = mCurrentBalance * monthlyInterest;
            mCurrentBalance += amount;

            if(mMakeWithdraws) {
                if (age.isBefore(mStartWithdrawalAge)) {
                    return createIncomeData(primaryAge, 0, mCurrentBalance);
                } else {
                    monthlyWithdraw = mCurrentBalance * initWithdrawPercent / 12;
                }
            } else {
                monthlyWithdraw = mCurrentBalance * initWithdrawPercent / 12;
            }
        }

        mCurrentAge = new AgeData(month);

        if(mMakeWithdraws && mCurrentAge.isBefore(mStartWithdrawalAge)) {
            monthlyWithdraw = 0;
        }

        return createIncomeData(mCurrentAge, monthlyWithdraw, mCurrentBalance);
    }

    @Override
    public double getMonthlyAmount(AgeData age) {
        IncomeData incomeData = getIncomeData(age);
        return incomeData.getMonthlyAmount();
    }

    @Override
    public double getBalance(AgeData age) {
        IncomeData incomeData = getIncomeData(age);
        return incomeData.getBalance();
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
