package com.intelliviz.retirementhelper.util;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.MilestoneData;

import static com.intelliviz.retirementhelper.util.RetirementConstants.WITHDRAW_MODE_AMOUNT;
import static com.intelliviz.retirementhelper.util.RetirementConstants.WITHDRAW_MODE_PERCENT;

/**
 * Created by edm on 10/20/2017.
 */

public class BalanceUtils {
    public static double getFutureBalance(double balance, int numMonths, double interest, double monthlyIncrease) {
        double cumulativeBalance = balance;
        for(int i = 0; i < numMonths; i++) {
            cumulativeBalance = getNewBalance(cumulativeBalance, interest, monthlyIncrease);
        }
        return cumulativeBalance;
    }

    public static double getNewBalance(double balance, double interest, double monthlyIncrease) {
        double interestEarned = getMonthlyAmountFromBalance(balance, interest);
        return monthlyIncrease + interestEarned + balance;
    }

    public static double getMonthlyAmountFromBalance(double balance, double interest) {
        double monthlyInterest = interest / 1200.0;
        return balance * monthlyInterest;
    }

    public static double getMonthlyAmount(double balance, int withdrawMode, double withdrawAmount) {
        double monthlyAmount;
        switch(withdrawMode) {
            case WITHDRAW_MODE_AMOUNT:
                monthlyAmount = withdrawAmount;
                break;
            case WITHDRAW_MODE_PERCENT:
                monthlyAmount = getMonthlyAmountFromBalance(balance, withdrawAmount);
                break;
            default:
                monthlyAmount = withdrawAmount;
        }
        return monthlyAmount;
    }

    public static MilestoneData getMilestoneData(AgeData startAge, AgeData endAge, double interest, double startBalance, double monthlyAmount) {
        AgeData age = endAge.subtract(startAge);
        int numMonthsInRetirement = age.getNumberOfMonths();
        double lastBalance = startBalance;
        double monthlyInterest = interest / 1200;

        int numMonths = 0;
        for(int mon = 0; mon < numMonthsInRetirement; mon++) {
            if(lastBalance <= 0) {
                break;
            }

            lastBalance = lastBalance - monthlyAmount;
            double monthlyIncrease = lastBalance * monthlyInterest;
            lastBalance = lastBalance + monthlyIncrease;
            numMonths++;
        }

        if(lastBalance < 0) {
            lastBalance = 0;
            numMonths--;
        }

        return new MilestoneData(startAge, endAge, null, monthlyAmount, startBalance, lastBalance, 0, numMonths);
    }
}
