package com.intelliviz.retirementhelper.data;

import android.os.Bundle;

import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_ANNUAL_PERCENT_INCREASE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_MONTHLY_ADDITION;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_BALANCE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_SOURCE_INTEREST;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_START_AGE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_STOP_AGE;
import static com.intelliviz.retirementhelper.util.RetirementConstants.EXTRA_INCOME_WITHDRAW_PERCENT;

/**
 * Created by edm on 12/30/2017.
 */

public abstract class BaseSavingsIncomeRules {
    private AgeData mCurrentAge;
    private AgeData mStartAge; // age at which withdraws begin
    private AgeData mEndAge; // end of life
    private AgeData mStopAge; // age at which monthly deposits stop
    private double mBalance; // balance
    private double mInterest; // annual interest (APR)
    private double mMonthlyDeposit; // amount that is deposited each month
    private double mWithdrawPercent; // The percentage of balance for initial withdraw.
    private double mAnnualPercentIncrease; // percent to increase withdraw

    /**
     * Constructor
     * @param birthDate The birthdate.
     * @param endAge The end retirement age.
     */
    public BaseSavingsIncomeRules(String birthDate, AgeData endAge) {
        mCurrentAge = SystemUtils.getAge(birthDate);
        mEndAge = endAge;
    }

    protected abstract double getPenaltyAmount(AgeData age, double amount);
    protected abstract boolean isPenalty(AgeData age);

    public void setValues(Bundle bundle) {
        mBalance = bundle.getDouble(EXTRA_INCOME_SOURCE_BALANCE);
        mInterest = bundle.getDouble(EXTRA_INCOME_SOURCE_INTEREST);
        mMonthlyDeposit = bundle.getDouble(EXTRA_INCOME_MONTHLY_ADDITION);
        mWithdrawPercent = bundle.getDouble(EXTRA_INCOME_WITHDRAW_PERCENT);
        mAnnualPercentIncrease = bundle.getDouble(EXTRA_ANNUAL_PERCENT_INCREASE);
        mStartAge = bundle.getParcelable(EXTRA_INCOME_START_AGE);
        mStopAge = bundle.getParcelable(EXTRA_INCOME_STOP_AGE);

        // no age can be before current age.
        if(mStartAge.isBefore(mCurrentAge)) {
            mStartAge = new AgeData(mCurrentAge.getNumberOfMonths());
        }

        if(mStopAge.isBefore(mCurrentAge)) {
            mStopAge = new AgeData(mCurrentAge.getNumberOfMonths());
        }
    }

    public List<BenefitData> getBenefitData() {
        int numMonths = 12;
        List<BenefitData> listAmountDate = new ArrayList<>();

        BenefitData benefitData = null;
        boolean done = false;
        while(true) {
            //benefitData = getBenefitDataForNextYear(benefitData);
            if(benefitData == null) {
                benefitData = getInitBenefitDataForNextMonth(new AgeData(mCurrentAge.getYear(), 0));
            } else {
                BenefitData bd = benefitData;
                for(int month = 0; month < numMonths; month++) {
                    bd = getBenefitDataForNextMonth(bd.getAge(),
                            bd.getBalance(), bd.getMonthlyAmountNoPenalty());
                    if(bd.getAge().getNumberOfMonths() > mEndAge.getNumberOfMonths()) {
                        done = true;
                        break;
                    }
                    //listAmountDate.add(bd);
                }
                if(done) {
                    break;
                }
                bd.setMonthlyAmount(bd.getMonthlyAmount() * (1 + mAnnualPercentIncrease/100));
                benefitData = bd;
                listAmountDate.add(bd);
            }
        }

        return listAmountDate;
    }

    public BenefitData getBenefitData(BenefitData benefitData) {
        return getBenefitDataForNextYear(benefitData);
    }

    BenefitData getBenefitDataForNextYear(BenefitData benefitData) {
        int numMonths = 1;
        if(benefitData == null) {
            return getInitBenefitDataForNextMonth(new AgeData(mCurrentAge.getYear(), 0));
        } else {
            BenefitData bd = benefitData;
            for(int month = 0; month < numMonths; month++) {
                bd =  getBenefitDataForNextMonth(bd.getAge(),
                        bd.getBalance(), bd.getMonthlyAmountNoPenalty());
            }
            bd.setMonthlyAmount(bd.getMonthlyAmount() * (1 + mAnnualPercentIncrease/100));

            return bd;
        }
    }

    BenefitData getInitBenefitDataForNextMonth(AgeData age) {
        double monthlyWithdrawAmount = 0;
        if(age.isBefore(mStartAge)) {
            monthlyWithdrawAmount = 0;
        } else if(age.getNumberOfMonths() == mStartAge.getNumberOfMonths()) {
            monthlyWithdrawAmount = getInitMonthlyWithdrawAmount(mBalance);
        }

        int status = checkBalance(mBalance, monthlyWithdrawAmount);
        double penaltyAmount = getPenaltyAmount(age, monthlyWithdrawAmount);

        return new BenefitData(age, monthlyWithdrawAmount, penaltyAmount, mBalance, status, isPenalty(age));
    }

    BenefitData getBenefitDataForNextMonth(AgeData age, double balance, double monthlyWithdrawAmount) {
        AgeData nextAge = new AgeData(age.getNumberOfMonths()+1);

        double monthlyDeposit;
        if(age.isBefore(mStopAge)) {
            monthlyDeposit = mMonthlyDeposit;
        } else {
            monthlyDeposit = 0;
        }

        double monthlyInterest = mInterest / 1200.0; // TODO make member variable

        balance = getBalance(balance, monthlyDeposit, monthlyInterest);

        if(nextAge.isBefore(mStartAge)) {
            monthlyWithdrawAmount = 0;
        } else if(nextAge.getNumberOfMonths() == mStartAge.getNumberOfMonths()) {
            monthlyWithdrawAmount = getInitMonthlyWithdrawAmount(balance);
        }

        balance = balance - monthlyWithdrawAmount;
        int status = checkBalance(balance, monthlyWithdrawAmount);
        double penaltyAmount = getPenaltyAmount(nextAge, monthlyWithdrawAmount);

        if(isPenalty(nextAge) && monthlyWithdrawAmount > 0) {
            status = RetirementConstants.BALANCE_STATE_LOW;
        }
        return new BenefitData(nextAge, monthlyWithdrawAmount, penaltyAmount, balance, status, isPenalty(nextAge));
    }

    private int checkBalance(double balance, double monthlyWithdrawAmount) {
        if(balance == 0) {
            return RetirementConstants.BALANCE_STATE_EXHAUSTED;
        } else {
            if(balance < monthlyWithdrawAmount*12) {
                return RetirementConstants.BALANCE_STATE_LOW;
            } else {
                return RetirementConstants.BALANCE_STATE_GOOD;
            }
        }
    }

    private static double getBalance(double balance, double monthlyDeposit, double monthlyInterest) {
        double interestEarned = balance * monthlyInterest;
        return monthlyDeposit + interestEarned + balance;
    }

    private double getInitMonthlyWithdrawAmount(double balance) {
        return balance * mWithdrawPercent / 1200;
    }
}
