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

    public List<BenefitData> getMonthlyAmountData() {
        AgeData age = mStartAge;
        if(age.getMonth() > 0) {
            age = new AgeData(age.getYear()+1, 0);
        }

        BenefitData benefitData = getInitAmountData(age);

        List<BenefitData> listAmountDate = new ArrayList<>();
        listAmountDate.add(benefitData);

        while(true) {
            // get next age
            AgeData nextAge = new AgeData(age.getYear()+1, 0);
            if(nextAge.isAfter(mEndAge)) {
                break;
            }

            double mWithdrawPercentIncrease = 0;
            benefitData = getNewAmountData(benefitData, nextAge, mWithdrawPercentIncrease);

            age = new AgeData(nextAge.getYear(), 0);

            listAmountDate.add(benefitData);
        }

        return listAmountDate;
    }

    BenefitData getInitAmountData(AgeData age) {
        int numMonths = mStartAge.diff(mCurrentAge);
        double balance = getFutureBalance(mBalance, numMonths, mInterest, mMonthlyAddition);

        double monthlyWithdrawAmount = getInitMonthlyWithdrawAmount(balance);

        monthlyWithdrawAmount = adjustMonthlyAmount(age, monthlyWithdrawAmount);
        boolean penalty = isPenalty(age);

        int balanceState = getBalanceStatus(balance, monthlyWithdrawAmount);
        if(balanceState == RetirementConstants.BALANCE_STATE_EXHAUSTED) {
            balance = 0;
        }
        return new BenefitData(age, monthlyWithdrawAmount, balance, balanceState, penalty);
    }

    BenefitData getNewAmountData(BenefitData amount, AgeData age, double withdrawAmountIncrease) {
        AgeData newAge = new AgeData(amount.getAge().getNumberOfMonths());
        int numMonths = newAge.diff(age);
        double balance = amount.getBalance();
        double monthlyWithdrawAmount = amount.getMonthlyAmount();
        balance = getNewBalance(numMonths, balance, monthlyWithdrawAmount, mInterest);

        int balanceState = getBalanceStatus(balance, monthlyWithdrawAmount);
        if(balanceState == RetirementConstants.BALANCE_STATE_EXHAUSTED) {
            balance = 0;
        }

        monthlyWithdrawAmount += withdrawAmountIncrease;
        monthlyWithdrawAmount = adjustMonthlyAmount(age, monthlyWithdrawAmount);
        return new BenefitData(age, monthlyWithdrawAmount, balance, balanceState, isPenalty(age));
    }

    int getBalanceStatus(double balance, double monthlyWithdrawAmount) {
        if(balance < 0) {
            return RetirementConstants.BALANCE_STATE_EXHAUSTED;
        } else {
            if(balance < monthlyWithdrawAmount*12) {
                return RetirementConstants.BALANCE_STATE_LOW;
            }
        }
        return RetirementConstants.BALANCE_STATE_GOOD;
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
