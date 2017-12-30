package com.intelliviz.retirementhelper.data;

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
    public BaseSavingsIncomeRules(String birthDate, AgeData endAge, AgeData startAge,
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

    public List<AmountData> getMonthlyAmountData() {
        AgeData age = mStartAge;
        if(age.getMonth() > 0) {
            age = new AgeData(age.getYear()+1, 0);
        }

        int numMonths = mStartAge.diff(mCurrentAge);
        double balance = getFutureBalance(mBalance, numMonths, mInterest, mMonthlyAddition);

        double monthlyWithdrawAmount = getInitMonthlyWithdrawAmount(balance);

        monthlyWithdrawAmount = adjustMonthlyAmount(age, monthlyWithdrawAmount);
        boolean penalty = isPenalty(age);

        List<AmountData> listAmountDate = new ArrayList<>();
        int balanceState = 2;
        AmountData amountData = new AmountData(age, monthlyWithdrawAmount, balance, balanceState, penalty);
        listAmountDate.add(amountData);

        while(true) {
            // get next age
            AgeData nextAge = new AgeData(age.getYear()+1, 0);
            if(nextAge.isAfter(mEndAge)) {
                break;
            }
            numMonths = nextAge.diff(age);

            age = new AgeData(nextAge.getYear(), 0);

            balance = getNewBalance(numMonths, balance, monthlyWithdrawAmount, mInterest);
            if(balance < 0) {
                balance = 0;
                balanceState = 0;
            } else {
                if(monthlyWithdrawAmount*12 > balance) {
                    balanceState = 1;
                }
            }

            // increase month withdraw amount
            double mWithdrawPercentIncrease = 0;
            double withdrawAmountIncrease = monthlyWithdrawAmount * mWithdrawPercentIncrease / 1200;
            monthlyWithdrawAmount += withdrawAmountIncrease;

            monthlyWithdrawAmount = adjustMonthlyAmount(age, monthlyWithdrawAmount);
            penalty = isPenalty(age);
            amountData = new AmountData(nextAge, monthlyWithdrawAmount, balance, balanceState, penalty);
            listAmountDate.add(amountData);
        }

        return listAmountDate;
    }

    double getInitMonthlyWithdrawAmount(double balance) {
        if(mWithdrawMode == WITHDRAW_MODE_PERCENT) {
            return balance * mWithdrawAmount / 1200;
        } else {
            return mWithdrawAmount;
        }
    }

    double getNewBalance(int numMonths, double balance, double monthlyWithdrawAmount, double annualInterest) {
        double monthlyInterest = annualInterest / 1200;
        double newBalance = balance;
        for(int month = 0; month < numMonths; month++) {
            newBalance -= monthlyWithdrawAmount;
            double monthlyIncrease = newBalance * monthlyInterest;
            newBalance += monthlyIncrease;
        }

        return newBalance;
    }

    private double getFutureBalance(double currentBalance, int numMonths, double annualInterest, double monthlyAddition) {
        double cumulativeBalance = currentBalance;
        for(int i = 0; i < numMonths; i++) {
            cumulativeBalance = getBalance(cumulativeBalance, annualInterest, monthlyAddition);
        }
        return cumulativeBalance;
    }

    private static double getBalance(double balance, double interest, double monthlyIncrease) {
        double interestEarned = getMonthlyAmountFromBalance(balance, interest);
        return monthlyIncrease + interestEarned + balance;
    }

    private static double getMonthlyAmountFromBalance(double balance, double interest) {
        double monthlyInterest = interest / 1200.0;
        return balance * monthlyInterest;
    }
}
