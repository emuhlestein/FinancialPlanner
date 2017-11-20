package com.intelliviz.retirementhelper.data;

import com.intelliviz.retirementhelper.util.BalanceUtils;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by edm on 10/19/2017.
 */

public class SavingsIncomeRules implements IncomeTypeRules {
    private AgeData mEndAge;
    private AgeData mCurrentAge;
    private double mBalance;
    private double mInterest;
    private double mMonthlyIncrease;
    private int mWithdrawMode;
    private double mWithdrawAmount;

    public SavingsIncomeRules(String birthDate, AgeData endAge,
                              double balance, double interest, double monthlyIncrease,  int withdrawMode, double withdrawAmount) {
        mCurrentAge = SystemUtils.getAge(birthDate);
        mEndAge = endAge;
        mBalance = balance;
        mInterest = interest;
        mMonthlyIncrease = monthlyIncrease;
        mWithdrawAmount = withdrawAmount;
        mWithdrawMode = withdrawMode;
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
}
