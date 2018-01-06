package com.intelliviz.retirementhelper.util;

import com.intelliviz.retirementhelper.data.AgeData;
import com.intelliviz.retirementhelper.data.BenefitData;
import com.intelliviz.retirementhelper.data.MilestoneData;

import static com.intelliviz.retirementhelper.R.string.balance;
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

    public static MilestoneData getMilestoneData(AgeData startAge, AgeData endAge, double interest,
                                                 double startBalance, double monthlyAmount, int withdrawMode, double withdrawAmount) {
        int numMonthsInRetirement = endAge.diff(startAge);
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

        return new MilestoneData(startAge, endAge, null, monthlyAmount, startBalance, lastBalance, interest, 0, numMonths, withdrawMode, withdrawAmount);
    }

    public static BenefitData getAmountData(AgeData startAge, AgeData endAge, double interest, double startBalance, int withdrawMode, double withdrawAmount) {
        int numMonths = endAge.diff(startAge);
        AgeData age = new AgeData(startAge.getYear(), startAge.getMonth());
        if(numMonths == 0) {
            return new BenefitData(age, getMonthlyAmount(balance, withdrawMode, withdrawAmount),  startBalance, 0, false);
        }
        double balance = startBalance;
        double monthlyInterest = interest / 1200;
        double monthlyAmount = 0;

        for(int mon = 0; mon < numMonths; mon++) {
            if(balance <= 0) {
                break;
            }

            monthlyAmount = getMonthlyAmount(balance, withdrawMode, withdrawAmount);
            balance = balance - monthlyAmount;
            double monthlyIncrease = balance * monthlyInterest;
            balance = balance + monthlyIncrease;

            if(balance < 0) {
                balance = 0;
                mon--;
                break;
            }
        }

        return new BenefitData(age, monthlyAmount,  balance, 0, false);
    }
}
