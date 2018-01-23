package com.intelliviz.retirementhelper.data;

import com.intelliviz.retirementhelper.util.RetirementConstants;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import static com.intelliviz.retirementhelper.util.RetirementConstants.WITHDRAW_MODE_PERCENT;

/**
 * Created by edm on 12/30/2017.
 */

public abstract class BaseSavingsIncomeRules {
    private AgeData mCurrentAge;
    private AgeData mStartAge;
    private AgeData mEndAge;
    private double mBalance;
    private double mInterest;
    private double mMonthlyAddition;
    private int mWithdrawMode;
    private double mWithdrawAmount;

    /**
     * Constructor
     * @param birthDate The birthdate.
     * @param endAge The end retirement age.
     * @param startAge The start retirement age.
     * @param balance The savings balance.
     * @param interest The annual interest.
     * @param monthlyAddition The monthly amount added to balance.
     * @param withdrawMode The withdraw mode: withdraw amount is either percent or dollar amount.
     * @param withdrawAmount The initial withdraw amount.
     */
    public BaseSavingsIncomeRules(String birthDate, AgeData startAge, AgeData endAge,
                                  double balance, double interest, double monthlyAddition,  int withdrawMode, double withdrawAmount) {
        mCurrentAge = SystemUtils.getAge(birthDate);
        mStartAge = startAge;
        mEndAge = endAge;
        mBalance = balance;
        mInterest = interest;
        mMonthlyAddition = monthlyAddition;
        mWithdrawMode = withdrawMode;
        mWithdrawAmount = withdrawAmount;
    }

    protected abstract double adjustMonthlyAmount(AgeData age, double amount);
    protected abstract boolean isPenalty(AgeData age);

    public List<BenefitData> getBenefitData() {
        AgeData age = mCurrentAge;
        if(age.getMonth() > 0) {
            age = new AgeData(age.getYear()+1, 0);
        }


        // get the balance at the start age.
        int numMonths = mStartAge.diff(mCurrentAge);
        //double balance = getFutureBalance(mBalance, numMonths, mInterest, mMonthlyAddition);
        double initWithdrawAmount;

        double monthlyWithdrawAmount = 0;
        boolean penalty = false;
        int balanceState = RetirementConstants.BALANCE_STATE_GOOD;
        List<BenefitData> listAmountDate = new ArrayList<>();
        double monthlyInterest = mInterest / 1200;
        double balance = mBalance;
        boolean initWithdraw = true;
        double annualWithdrawIncrease = 0;
        for(int year = age.getYear(); year < mEndAge.getYear(); year++) {
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

    public double getBalanceForAge(AgeData age) {
        if(age.isBefore(mCurrentAge)) {
            return 0;
        }

        int numMonths = mCurrentAge.diff(age);
        return getFutureBalance(mBalance, numMonths, mInterest, mMonthlyAddition);
    }

    public BenefitData getBenefitForAge(AgeData age) {
        if (age.isBefore(mCurrentAge)) {
            return null;
        }

        // assume that after the start date, there are no more monthly additions.
        double monthlyAddition;
        if(age.isAfter(mStartAge)) {
            monthlyAddition = 0;
        } else {
            monthlyAddition = mMonthlyAddition;
        }
        int numMonths = mCurrentAge.diff(age);
        double balance = getFutureBalance(mBalance, numMonths, mInterest, monthlyAddition);

        double monthlyWithdrawAmount;
        boolean penalty;
        int balanceState = RetirementConstants.BALANCE_STATE_GOOD;

        if (age.isBefore(mStartAge)) {
            monthlyWithdrawAmount = 0; // no withdraws before start date
            penalty = false;
            balanceState = RetirementConstants.BALANCE_STATE_GOOD;
        } else {
            monthlyWithdrawAmount = getMonthlyWithdrawAmount(age);
            monthlyWithdrawAmount = adjustMonthlyAmount(age, monthlyWithdrawAmount);
            penalty = isPenalty(age);
        }

        return new BenefitData(age, monthlyWithdrawAmount, balance, balanceState, penalty);
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

    private double getMonthlyWithdrawAmount(AgeData age) {
        // getList balance at start date
        double mWithdrawPercentIncrease = 0; // make class variable
        int numMonths = mCurrentAge.diff(mStartAge);
        double balanceAtStartAge = getFutureBalance(mBalance, numMonths, mInterest, mMonthlyAddition);
        double monthlyWithdrawAmount = getInitMonthlyWithdrawAmount(balanceAtStartAge);
        int numYears = age.getYear() - mStartAge.getYear();
        for(int i = 0; i < numYears; i++) {
            double annualWithdrawIncrease = monthlyWithdrawAmount * mWithdrawPercentIncrease;
            monthlyWithdrawAmount += annualWithdrawIncrease;
        }

        return monthlyWithdrawAmount;
    }

    private double getInitMonthlyWithdrawAmount(double balance) {
        if(mWithdrawMode == WITHDRAW_MODE_PERCENT) {
            return getInitMonthlyWithdrawAmount(balance, mWithdrawAmount);
        } else {
            return mWithdrawAmount;
        }
    }

    private double getInitMonthlyWithdrawAmount(double balance, double percent) {
        return balance * percent / 1200;
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
