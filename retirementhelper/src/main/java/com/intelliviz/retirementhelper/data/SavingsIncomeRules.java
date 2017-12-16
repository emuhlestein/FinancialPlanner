package com.intelliviz.retirementhelper.data;

import android.os.Bundle;

import com.intelliviz.retirementhelper.util.BalanceUtils;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.intelliviz.retirementhelper.util.RetirementConstants.WITHDRAW_MODE_PERCENT;

/**
 * Created by edm on 10/19/2017.
 */

public class SavingsIncomeRules implements IncomeTypeRules {
    private AgeData mEndAge;
    private AgeData mCurrentAge;
    private AgeData mStartAge;
    private double mBalance;
    private double mInterest;
    private double mMonthlyIncrease;
    private int mWithdrawMode;
    private double mWithdrawAmount;

    public SavingsIncomeRules(String birthDate, AgeData endAge, AgeData startAge,
                              double balance, double interest, double monthlyIncrease,  int withdrawMode, double withdrawAmount) {
        mCurrentAge = SystemUtils.getAge(birthDate);
        mEndAge = endAge;
        mStartAge = startAge;
        mBalance = balance;
        mInterest = interest;
        mMonthlyIncrease = monthlyIncrease;
        mWithdrawAmount = withdrawAmount;
        mWithdrawMode = withdrawMode;
    }

    @Override
    public void setValues(Bundle bundle) {
    }

    @Override
    public MilestoneData getMilestone(AgeData age) {
        int numMonths = age.diff(mCurrentAge);
        double futureBalance = BalanceUtils.getFutureBalance(mBalance, numMonths, mInterest, mMonthlyIncrease);

        double monthlyAmount = BalanceUtils.getMonthlyAmount(mBalance, mWithdrawMode, mWithdrawAmount);
        MilestoneData milestoneData =
                BalanceUtils.getMilestoneData(age, mEndAge, mInterest, futureBalance, monthlyAmount, mWithdrawMode, mWithdrawAmount);

        return milestoneData;
    }

    @Override
    public List<AgeData> getAges() {
        return Collections.emptyList();
    }


    // TODO need to refactor this
    public List<AmountData> getMonthlyAmountData() {
        AgeData age = mStartAge;

        int numMonths = mStartAge.diff(mCurrentAge);
        double balance = BalanceUtils.getFutureBalance(mBalance, numMonths, mInterest, mMonthlyIncrease);
        double monthlyWithdrawAmount = getInitMonthlyWithdrawAmount(balance);

        List<AmountData> listAmountDate = new ArrayList<>();

        int balanceState = 2;
        AmountData amountData = new AmountData(age, monthlyWithdrawAmount, balance, balanceState, false);
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

            amountData = new AmountData(nextAge, monthlyWithdrawAmount, balance, balanceState, false);
            listAmountDate.add(amountData);
        }

        return listAmountDate;
    }

    // TODO need to refactor this
    private double getInitMonthlyWithdrawAmount(double balance) {
        if(mWithdrawMode == WITHDRAW_MODE_PERCENT) {
            return balance * mWithdrawAmount / 1200;
        } else {
            return mWithdrawAmount;
        }
    }

    // TODO need to refactor this
    private double getNewBalance(int numMonths, double balance, double monthlyWithdrawAmount, double annualInterest) {
        double monthlyInterest = annualInterest / 1200;
        double newBalance = balance;
        for(int month = 0; month < numMonths; month++) {
            newBalance -= monthlyWithdrawAmount;
            double monthlyIncrease = newBalance * monthlyInterest;
            newBalance += monthlyIncrease;
        }

        return newBalance;
    }

}
