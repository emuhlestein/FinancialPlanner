package com.intelliviz.retirementhelper.data;

import android.os.Bundle;

import java.util.Collections;
import java.util.List;

/**
 * Created by edm on 10/19/2017.
 */

public class SavingsIncomeRules extends BaseSavingsIncomeRules implements IncomeTypeRules {

    public SavingsIncomeRules(String birthDate, AgeData endAge, AgeData startAge,
                              double balance, double interest, double monthlyAddition,  int withdrawMode, double withdrawAmount) {
        super(birthDate, endAge, startAge, balance, interest, monthlyAddition, withdrawMode, withdrawAmount);
    }

    @Override
    protected double adjustMonthlyAmount(AgeData age, double amount) {
        return amount;
    }

    @Override
    protected boolean isPenalty(AgeData age) {
        return false;
    }

    @Override
    public void setValues(Bundle bundle) {
    }

    @Override
    @Deprecated
    public MilestoneData getMilestone(AgeData age) {
        /*
        int numMonths = age.diff(mCurrentAge);
        double futureBalance = BalanceUtils.getFutureBalance(mBalance, numMonths, mInterest, mMonthlyIncrease);

        double monthlyAmount = BalanceUtils.getMonthlyAmount(mBalance, mWithdrawMode, mWithdrawAmount);
        MilestoneData milestoneData =
                BalanceUtils.getMilestoneData(age, mEndAge, mInterest, futureBalance, monthlyAmount, mWithdrawMode, mWithdrawAmount);
*/
        return null;
    }

    @Override
    public List<AgeData> getAges() {
        return Collections.emptyList();
    }
}
