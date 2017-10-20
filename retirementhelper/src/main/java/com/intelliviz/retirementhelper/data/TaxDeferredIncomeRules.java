package com.intelliviz.retirementhelper.data;

import com.intelliviz.retirementhelper.util.BalanceUtils;
import com.intelliviz.retirementhelper.util.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.intelliviz.retirementhelper.util.BalanceUtils.getFutureBalance;
import static com.intelliviz.retirementhelper.util.BalanceUtils.getMonthlyAmount;

/**
 * Created by edm on 10/18/2017.
 */

public class TaxDeferredIncomeRules implements IncomeTypeRules {
    private AgeData mMinAge;
    private AgeData mCurrentAge;
    private AgeData mEndAge;
    private double mBalance;
    private double mInterest;
    private double mMonthlyIncrease;
    private double mPenalty;
    private int mWithdrawMode;
    private double mWithdrawAmount;

    public TaxDeferredIncomeRules(String birthDate, AgeData endAge, double balance,
                                  double interest, double monthlyIncrease, int withdrawMode, double withdrawAmount) {
        mCurrentAge = SystemUtils.getAge(birthDate);
        mMinAge = new AgeData(59, 6);
        mInterest = interest;
        mMonthlyIncrease = monthlyIncrease;
        mPenalty = 0.10;
        mBalance = balance;
        mEndAge = endAge;
        mWithdrawAmount = withdrawAmount;
        mWithdrawMode = withdrawMode;
    }

    @Override
    public double getMonthlyBenefitForAge(AgeData age) {
        if(age.isBefore(mCurrentAge)) {
            return 0;
        }
        AgeData diffAge = age.subtract(mCurrentAge);
        int numMonths = diffAge.getNumberOfMonths();
        double futureBalance = getFutureBalance(mBalance, numMonths, mInterest, mMonthlyIncrease);

        double monthlyAmount = getMonthlyAmount(mBalance, mWithdrawMode, mWithdrawAmount);
        MilestoneData milestoneData = BalanceUtils.getMilestoneData(age, mEndAge, mInterest, futureBalance, monthlyAmount);

        if(age.isBefore(mMinAge)) {

        }

        return milestoneData.getMonthlyBenefit();
    }

    @Override
    public List<AgeData> getAges() {
        return new ArrayList<>(Arrays.asList(mMinAge));
    }

    public MilestoneData getMilestone(AgeData age) {
        double monthlyBenefit = getMonthlyBenefitForAge(age);
        return new MilestoneData(age, null, mMinAge, monthlyBenefit, 0, 0, 0, 0);
    }
}
