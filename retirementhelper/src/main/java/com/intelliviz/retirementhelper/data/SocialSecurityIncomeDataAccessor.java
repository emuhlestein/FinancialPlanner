package com.intelliviz.retirementhelper.data;

/**
 * Created by edm on 6/5/2018.
 */

public class SocialSecurityIncomeDataAccessor implements IncomeDataAccessor {
    private AgeData mStartAge;
    private double mMonthlyAmount;

    public SocialSecurityIncomeDataAccessor(AgeData startAge, double monthlyAmount) {
        mStartAge = startAge;
        mMonthlyAmount = monthlyAmount;
    }
    @Override
    public BenefitData getBenefitData(AgeData age) {
        if(age.isOnOrAfter(mStartAge)) {
            return new BenefitData(age, mMonthlyAmount, 0, 0, false);
        } else {
            return new BenefitData(age, 0, 0, 0, false);
        }
    }
}
