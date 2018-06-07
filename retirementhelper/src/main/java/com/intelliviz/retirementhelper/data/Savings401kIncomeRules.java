package com.intelliviz.retirementhelper.data;

/**
 * Created by edm on 10/18/2017.
 */

public class Savings401kIncomeRules extends BaseSavingsIncomeRules implements IncomeTypeRules {
    private static final double PENALTY_PERCENT = 10.0;
    private static final AgeData PENALTY_AGE = new AgeData(59, 6);
    private AgeData mMinAge;
    private double mPenalty;

    public Savings401kIncomeRules(String birthDate,  AgeData endAge) {
        super(birthDate, endAge);
        mMinAge = PENALTY_AGE;
        mPenalty = PENALTY_PERCENT;
    }

    @Override
    protected double getPenaltyAmount(AgeData age, double amount) {
        if(age.isBefore(PENALTY_AGE)) {
            return amount * PENALTY_PERCENT / 1200;
        } else {
            return 0;
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
    public IncomeDataAccessor getIncomeDataAccessor() {
        return new Savings401kIncomeDataAccessor(getBenefitData());
    }
}
