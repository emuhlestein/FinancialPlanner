package com.intelliviz.retirementhelper.util;

import com.intelliviz.data.IncomeData;
import com.intelliviz.lowlevel.data.AgeData;

import static com.intelliviz.lowlevel.util.RetirementConstants.WITHDRAW_MODE_AMOUNT;
import static com.intelliviz.lowlevel.util.RetirementConstants.WITHDRAW_MODE_PERCENT;

/**
 * Created by edm on 10/20/2017.
 */

public class BalanceUtils {

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

    public static IncomeData getIncomeData(AgeData startAge, AgeData endAge, double interest, double startBalance, int withdrawMode, double withdrawAmount) {
        int numMonths = endAge.diff(startAge);
        double balance = startBalance;
        AgeData age = new AgeData(startAge.getYear(), startAge.getMonth());
        if(numMonths == 0) {
            return new IncomeData(age, getMonthlyAmount(balance, withdrawMode, withdrawAmount),  startBalance, 0, false);
        }

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

        return new IncomeData(age, monthlyAmount,  balance, 0, false);
    }
}
