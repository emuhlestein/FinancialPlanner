package com.intelliviz.retirementhelper.data;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.intelliviz.retirementhelper.util.RetirementConstants.WITHDRAW_MODE_PERCENT;

/**
 * Created by edm on 10/18/2017.
 */

public class Savings401kIncomeRules extends BaseSavingsIncomeRules implements IncomeTypeRules {
    private static final double PENALTY_PERCENT = 10.0;
    private static final AgeData PENALTY_AGE = new AgeData(59, 6);
    private AgeData mMinAge;
    private double mPenalty;

    public Savings401kIncomeRules(String birthDate, AgeData startAge, AgeData endAge, double balance,
                                  double interest, double monthlyAddition, int withdrawMode, double withdrawAmount) {
        super(birthDate, startAge, endAge, balance, interest, monthlyAddition, withdrawMode, withdrawAmount);
        mMinAge = PENALTY_AGE;
        mPenalty = PENALTY_PERCENT;
    }

    @Override
    protected double adjustMonthlyAmount(AgeData age, double amount) {
        double penaltyPercent = (100 - PENALTY_PERCENT)/1200;
        if(isPenalty(age)) {
            return amount * penaltyPercent;
        } else {
            return amount;
        }
    }

    @Override
    protected boolean isPenalty(AgeData age) {
        if(age.isBefore(PENALTY_AGE)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setValues(Bundle bundle) {
    }

    public MilestoneData getMilestone(AgeData age) {
        return null;
    }

    @Override
    public List<AgeData> getAges() {
        return new ArrayList<>(Arrays.asList(mMinAge));
    }

    private TaxDeferredData getMilestoneData(AgeData startAge, AgeData endAge, double interest,
                                                 double startBalance, int withdrawMode, double withdrawAmount) {
        if(!startAge.isBefore(endAge)) {
            return null;
        }
        int numMonthsInRetirement = endAge.diff(startAge);
        double lastBalance = startBalance;
        double monthlyInterest = interest / 1200;

        double initialAmount;
        if(withdrawMode == WITHDRAW_MODE_PERCENT) {
            double percent = withdrawAmount / 1200;
            initialAmount = startBalance * percent;
        } else {
            initialAmount = withdrawAmount;
        }

        double monthlyAmount = initialAmount;
        double monthlyAmountIncrease = 0;

        int numMonths = 0;
        AgeData countAge = new AgeData();
        for(int mon = 0; mon < numMonthsInRetirement; mon++) {
            if(lastBalance <= 0) {
                break;
            }

            lastBalance = lastBalance - monthlyAmount;
            double monthlyIncrease = lastBalance * monthlyInterest;
            lastBalance = lastBalance + monthlyIncrease;

            countAge.add(1);
            if(countAge.getMonth() == 0) {
                // new year, increase withdraw amount
                double amountToIncrease = monthlyAmount * monthlyAmountIncrease;
                monthlyAmount += amountToIncrease;
            }

            numMonths++;
        }

        if(lastBalance < 0) {
            lastBalance = 0;
            numMonths--;
        }

        double annutalAmount = monthlyAmount * 12;
        int status;
        if(lastBalance == 0) {
            status = 0;
        } else if(lastBalance < annutalAmount) {
            status = 1;
        } else {
            status = 2;
        }

        // Need the following:
        // Start Balance, End Balance, num months, init withdraw amount, final withdraw amount, did money run out
        return new TaxDeferredData(startAge, numMonths, startBalance, lastBalance, initialAmount, monthlyAmount, status);
    }
}
