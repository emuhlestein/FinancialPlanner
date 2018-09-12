package com.intelliviz.data;

import android.os.Bundle;

import com.intelliviz.lowlevel.data.AgeData;
import com.intelliviz.lowlevel.util.AgeUtils;

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

    /**
     * Constructor
     *
     * @param ownerBirthDate The birthdate.
     * @param endAge    The end retirement age.
     */
    BaseSavingsIncomeRules(String ownerBirthDate, AgeData endAge, String otherBirthdate) {
        mOwnerBirthdate = ownerBirthDate;
        mEndAge = endAge;
        mOtherBirthdate = otherBirthdate;
    }

    protected abstract IncomeData createIncomeData(AgeData age, double monthlyAmount, double balance);

    protected abstract IncomeDataAccessor getIncomeDataAccessor();

    public void setValues(Bundle bundle) {
        mBalance = bundle.getDouble(EXTRA_INCOME_SOURCE_BALANCE);
        mInterest = bundle.getDouble(EXTRA_INCOME_SOURCE_INTEREST);
        mMonthlyDeposit = bundle.getDouble(EXTRA_INCOME_MONTHLY_ADDITION);
        mInitialWithdrawPercent = bundle.getDouble(EXTRA_INCOME_WITHDRAW_PERCENT);
        mAnnualPercentIncrease = bundle.getDouble(EXTRA_ANNUAL_PERCENT_INCREASE);
        mStartAge = bundle.getParcelable(EXTRA_INCOME_START_AGE);
        mStopAge = bundle.getParcelable(EXTRA_INCOME_STOP_AGE);
        mShowMonths = bundle.getInt(EXTRA_INCOME_SHOW_MONTHS) == 1;

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
            //listAmountDate.add(new IncomeData(age, monthlyWithdraw, 0, balance, 0, false));

            if (age.isAfter(mStopAge)) {
                monthlyDeposit = 0;
            }
            balance += monthlyDeposit;

            double amount = balance * monthlyInterest;
            balance += amount;
        }

        return listAmountDate;
    }

//    public IncomeData getIncomeData(IncomeData benefitData) {
//        return getBenefitDataForNextYear(benefitData);
//    }

//    private IncomeData getBenefitDataForNextYear(IncomeData benefitData) {
//        int numMonths = 1;
//        if (benefitData == null) {
//            return null; //getInitBenefitDataForNextMonth(new AgeData(mCurrentAge.getYear(), 0));
//        } else {
//            IncomeData bd = benefitData;
//            for (int month = 0; month < numMonths; month++) {
//                bd = getBenefitDataForNextMonth(bd.getAge(),
//                        bd.getBalance(), bd.getMonthlyAmountNoPenalty());
//            }
//            bd.setMonthlyAmount(bd.getMonthlyAmount() * (1 + mAnnualPercentIncrease / 100));
//
//            return bd;
//        }
//    }
//
//    private IncomeData getBenefitDataForNextMonth(AgeData age, double balance, double monthlyWithdrawAmount) {
//        AgeData nextAge = new AgeData(age.getNumberOfMonths() + 1);
//
//        double monthlyDeposit;
//        if (age.isBefore(mStopAge)) {
//            monthlyDeposit = mMonthlyDeposit;
//        } else {
//            monthlyDeposit = 0;
//        }
//
//        double monthlyInterest = mInterest / 1200.0; // TODO make member variable
//
//        balance = getBalance(balance, monthlyDeposit, monthlyInterest);
//
//        if (nextAge.isBefore(mStartAge)) {
//            monthlyWithdrawAmount = 0;
//        } else if (nextAge.getNumberOfMonths() == mStartAge.getNumberOfMonths()) {
//            monthlyWithdrawAmount = getInitMonthlyWithdrawAmount(balance);
//        }
//
//        balance = balance - monthlyWithdrawAmount;
//        int status = checkBalance(balance, monthlyWithdrawAmount);
//        double penaltyAmount = getPenaltyAmount(nextAge, monthlyWithdrawAmount);
//
//        if (isPenalty(nextAge) && monthlyWithdrawAmount > 0) {
//            status = getPenaltyStatus();
//        }
//        return new IncomeData(nextAge, monthlyWithdrawAmount, penaltyAmount, balance, status, isPenalty(nextAge));
//    }

//    private int checkBalance(double balance, double monthlyWithdrawAmount) {
//        if (balance == 0) {
//            return RetirementConstants.BALANCE_STATE_EXHAUSTED;
//        } else {
//            if (balance < monthlyWithdrawAmount * 12) {
//                return RetirementConstants.BALANCE_STATE_LOW;
//            } else {
//                return RetirementConstants.BALANCE_STATE_GOOD;
//            }
//        }
//    }
//
//    private static double getBalance(double balance, double monthlyDeposit, double monthlyInterest) {
//        double interestEarned = balance * monthlyInterest;
//        return monthlyDeposit + interestEarned + balance;
//    }
//
//    private double getInitMonthlyWithdrawAmount(double balance) {
//        return balance * mInitialWithdrawPercent / 1200;
//    }
}
