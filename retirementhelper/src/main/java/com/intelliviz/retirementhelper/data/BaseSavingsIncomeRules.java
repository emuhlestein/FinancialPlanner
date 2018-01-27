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
    private AgeData mStartAge;
    private AgeData mEndAge;
    private AgeData mStopAge;
    private double mBalance;
    private double mInterest;
    private double mMonthlyAddition;
    private double mWithdrawPercent;
    private double mAnnualPercentIncrease;

    /**
     * Constructor
     * @param birthDate The birthdate.
     * @param endAge The end retirement age.
     */
    public BaseSavingsIncomeRules(String birthDate, AgeData endAge) {
        mCurrentAge = SystemUtils.getAge(birthDate);
        mEndAge = endAge;
    }

    protected abstract double adjustMonthlyAmount(AgeData age, double amount);
    protected abstract boolean isPenalty(AgeData age);

    public void setValues(Bundle bundle) {
        mBalance = bundle.getDouble(EXTRA_INCOME_SOURCE_BALANCE);
        mInterest = bundle.getDouble(EXTRA_INCOME_SOURCE_INTEREST);
        mMonthlyAddition = bundle.getDouble(EXTRA_INCOME_MONTHLY_ADDITION);
        mWithdrawPercent = bundle.getDouble(EXTRA_INCOME_WITHDRAW_PERCENT);
        mAnnualPercentIncrease = bundle.getDouble(EXTRA_ANNUAL_PERCENT_INCREASE);
        mStartAge = bundle.getParcelable(EXTRA_INCOME_START_AGE);
        mStopAge = bundle.getParcelable(EXTRA_INCOME_STOP_AGE);
    }

    public List<BenefitData> getBenefitData() {
        AgeData age = mCurrentAge;
        if(age.getMonth() > 0) {
            age = new AgeData(age.getYear()+1, 0);
        }

        double monthlyWithdrawAmount = 0;
        boolean penalty = false;
        int balanceState = RetirementConstants.BALANCE_STATE_GOOD;
        List<BenefitData> listAmountDate = new ArrayList<>();
        double monthlyInterest = mInterest / 1200;
        double balance = mBalance;
        boolean initWithdraw = true;
        double annualWithdrawIncrease = 0;
        for(int year = age.getYear(); year <= mEndAge.getYear(); year++) {
            age = new AgeData(year, 0);

            if(age.isAfter(mStartAge)) {
                // start doing withdraws
                if(initWithdraw) {
                    initWithdraw = false;
                    monthlyWithdrawAmount = getInitMonthlyWithdrawAmount(balance);
                } else {
                    monthlyWithdrawAmount += annualWithdrawIncrease;
                }
            }

            for(int i = 0; i < 12; i++) {
                double monthlyIncrease = balance * monthlyInterest;
                balance += monthlyIncrease;
                if(age.isAfter(mStartAge)) {
                    balance -= monthlyWithdrawAmount;
                } else {
                    balance += mMonthlyAddition;
                }
            }

            listAmountDate.add(new BenefitData(age, monthlyWithdrawAmount, balance, balanceState, penalty));
        }

        return listAmountDate;
    }

    public BenefitData getBenefitData(BenefitData benefitData) {
        int numMonths = 12;
        double monthlyWithdrawAmount;
        if(benefitData == null) {
            monthlyWithdrawAmount = getInitMonthlyWithdrawAmount(mBalance);
            return new BenefitData(mCurrentAge, monthlyWithdrawAmount, mBalance, RetirementConstants.BALANCE_STATE_GOOD, false);
        } else {
            AgeData age = new AgeData(benefitData.getAge().getNumberOfMonths() + numMonths);
            monthlyWithdrawAmount = benefitData.getMonthlyAmount();
            double monthlyAddition;
            if(age.isAfter(mStartAge)) {
                monthlyAddition = 0;
            } else {
                monthlyAddition = mMonthlyAddition;
            }
            double balance = getFutureBalance(benefitData.getBalance(), numMonths, mInterest, monthlyAddition);
            return new BenefitData(age, monthlyWithdrawAmount, balance, RetirementConstants.BALANCE_STATE_GOOD, false);
        }
    }

    private double getInitMonthlyWithdrawAmount(double balance) {
        return balance * mWithdrawPercent / 1200;
    }

    private double getFutureBalance(double currentBalance, int numMonths, double annualInterest, double monthlyAddition) {
        double monthlyInterest = annualInterest / 1200.0;
        double cumulativeBalance = currentBalance;
        for(int i = 0; i < numMonths; i++) {
            cumulativeBalance = getBalance(cumulativeBalance, monthlyAddition, monthlyInterest);
        }
        return cumulativeBalance;
    }

    private static double getBalance(double balance, double monthlyAddition, double monthlyInterest) {
        double interestEarned = balance * monthlyInterest;
        return monthlyAddition + interestEarned + balance;
    }
}
